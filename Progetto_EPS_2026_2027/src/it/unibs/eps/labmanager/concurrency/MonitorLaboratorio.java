package it.unibs.eps.labmanager.concurrency;

import it.unibs.eps.labmanager.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestore thread-safe del catalogo strumenti e delle richieste degli utenti.
 * <p>
 * Questa classe centralizza la logica di prestito e restituzione per evitare conflitti 
 * tra utenti che tentano di acquisire la stessa attrezzatura contemporaneamente. Mantiene 
 * traccia in modo sicuro di chi ha in uso uno strumento, chi è in attesa, e aggiorna 
 * costantemente lo storico degli eventi.
 * </p>
 *
 * @author Lorenzo Manna
 * @author Simone Biagi
 */ public class MonitorLaboratorio {
    private final Catalogo catalogo;
    private final List<EventLog> registroEventi;
    private final List<InfoRichiesta> listaRichieste;
    
    private ListenerLaboratorio listener;
    
    /**
     * Inizializza il Monitor associandolo al catalogo degli strumenti.
     * 
     * @param catalogo Il catalogo di partenza (le cui operazioni di modifica 
     *                 verranno protette da questo monitor).
     * @author Simone Biagi
     */
    public MonitorLaboratorio(Catalogo catalogo) {
        this.catalogo = catalogo;
        this.registroEventi = new ArrayList<>();
        this.listaRichieste = new ArrayList<>();
    }
    
    /**
     * Richiede un prestito per una specifica categoria di strumento.
     * <p>
     * Se lo strumento è disponibile, viene assegnato immediatamente. Se non è 
     * disponibile, il thread chiamante viene messo in attesa ({@code wait()}) finché 
     * non avviene una restituzione. L'intera verifica e assegnazione avviene in 
     * modo atomico.
     * </p>
     * 
     * @param tipo       La tipologia di strumento richiesta.
     * @param nomeUtente Il nome dell'utente (thread) che effettua la richiesta.
     * @return L'istanza dello {@link Strumento} assegnato, oppure {@code null} se 
     *         la richiesta viene rifiutata, annullata (es. tipo rimosso) o interrotta.
     * @author Simone Biagi
     */
    public Strumento richiediStrumento(TipoStrumento tipo, String nomeUtente) {
        Strumento assegnato = null;
        
        // Entriamo nel monitor per gestire in modo atomico la disponibilità
        synchronized (this) {
            if (!catalogo.esisteTipoStrumento(tipo)) {
                registraEvento("Richiesta rifiutata per " + nomeUtente + ": nessun " + tipo + " in catalogo");
                aggiornaOCreaRichiesta(nomeUtente, tipo, StatoPrestito.RIFIUTATO);
            } else {
                Strumento strumentoTrovato = catalogo.trovaStrumentoDisponibile(tipo);
                
                // Se lo strumento non è subito disponibile, si registra l'attesa una sola volta
                if (strumentoTrovato == null) {
                    registraEvento(nomeUtente + " in attesa di " + tipo);
                    aggiornaOCreaRichiesta(nomeUtente, tipo, StatoPrestito.IN_ATTESA);
                }
                
                // Un thread in attesa deve ricontrollare la condizione al risveglio
                while (strumentoTrovato == null) {
                    // Controllo extra per evitare deadlock se il tipo di strumento viene rimosso dal catalogo
                    if (!catalogo.esisteTipoStrumento(tipo)) {
                        registraEvento("Richiesta annullata per " + nomeUtente + ": " + tipo + " rimosso.");
                        aggiornaOCreaRichiesta(nomeUtente, tipo, StatoPrestito.RIFIUTATO);
                        break; 
                    }
                    
                    try {
                        // Mette in attesa la richiesta
                        wait();
                    } catch (InterruptedException e) {
                        // L'interruzione viene gestita senza lasciare lo stato del laboratorio incoerente
                        Thread.currentThread().interrupt();
                        registraEvento("Richiesta interrotta per " + nomeUtente);
                        aggiornaOCreaRichiesta(nomeUtente, tipo, StatoPrestito.RIFIUTATO);
                        break;
                    }
                    // Al risveglio, delega nuovamente la ricerca al Catalogo
                    strumentoTrovato = catalogo.trovaStrumentoDisponibile(tipo);
                }
                
                // Il controllo della disponibilità e l'assegnazione avvengono come un'unica operazione protetta
                if (strumentoTrovato != null) {
                    strumentoTrovato.setStato(StatoStrumento.IN_PRESTITO);
                    registraEvento(nomeUtente + " ottiene " + strumentoTrovato.getId());
                    aggiornaOCreaRichiesta(nomeUtente, tipo, StatoPrestito.IN_PRESTITO);
                    assegnato = strumentoTrovato;
                }
            }
        } // Fine della sezione critica. Il monitor viene rilasciato.
        
        // Chiamata all'interfaccia FUORI dal blocco synchronized per non bloccare l'interfaccia
        notificaCambioStato(); 
        return assegnato;
    }

    /**
     * Registra un evento nel log storico del laboratorio.
     * <p>
     * Metodo privato sicuro perché richiamato esclusivamente all'interno 
     * di blocchi {@code synchronized}. Non richiede un ulteriore lock.
     * </p>
     * 
     * @param descrizione Il testo dell'evento da registrare.
     * @author Simone Biagi
     */
    private void registraEvento(String descrizione) {
        registroEventi.add(new EventLog(descrizione));
    }
    
    /**
     * Gestisce la restituzione di uno strumento al laboratorio.
     * <p>
     * Aggiorna lo stato dello strumento a disponibile e invoca {@code notifyAll()} 
     * per risvegliare tutti i thread che erano bloccati in attesa nel metodo 
     * {@code richiediStrumento}.
     * </p>
     * 
     * @param strumento Lo strumento da restituire al catalogo.
     * @param nomeUtente Il nome dell'utente che restituisce lo strumento.
     * @author Simone Biagi
     */
    public void restituisciStrumento(Strumento strumento, String nomeUtente) {
        synchronized (this) {
            strumento.setStato(StatoStrumento.DISPONIBILE);
            registraEvento(nomeUtente + " restituisce " + strumento.getId());
            aggiornaOCreaRichiesta(nomeUtente, strumento.getTipo(), StatoPrestito.RESTITUITO);
            
            // La restituzione deve rendere possibile il progresso delle richieste in attesa
            notifyAll(); 
        } // Rilascio del monitor
        notificaCambioStato(); 
    }
    
    /**
     * Recupera lo storico di tutti gli eventi registrati nel sistema.
     * 
     * @return Una copia non modificabile della lista degli eventi (Deep copy non necessaria, gli eventi sono immutabili).
     * @author Simone Biagi
     */
    public synchronized List<EventLog> getRegistroEventi() {
        return List.copyOf(registroEventi); 
    }
    
    /**
     * Recupera lo stato attuale di tutte le richieste (attive o terminate) per la GUI.
     * 
     * @return Una copia della lista delle richieste per evitare modifiche esterne della lista
     */
    public synchronized List<InfoRichiesta> getListaRichieste() {
        List<InfoRichiesta> copia = new ArrayList<>();
        for (InfoRichiesta r : listaRichieste) {
            copia.add(new InfoRichiesta(r.getNomeUtente(), r.getTipoStrumento(), r.getStato()));
        }
        return copia; 
    }

    /**
     * Aggiunge un nuovo strumento al catalogo del laboratorio.
     * <p>
     * Segnala il nuovo inserimento risvegliando eventuali thread in attesa, 
     * i quali controlleranno se il nuovo strumento corrisponde alla loro richiesta.
     * </p>
     * 
     * @param strumento Il nuovo strumento da aggiungere.
     * @author Lorenzo Manna
     */
    public void aggiungiStrumento(Strumento strumento) {
        synchronized (this) {
            catalogo.aggiungiStrumento(strumento);
            registraEvento("Nuovo strumento aggiunto: " + strumento.getId() + " (" + strumento.getTipo() + ")");
            // Risveglia eventuali thread che attendevano uno strumento di questo tipo
            notifyAll();
        }
        notificaCambioStato();
    }

    /**
     * Rimuove uno strumento esistente dal catalogo del laboratorio.
     * 
     * @param strumento Lo strumento da rimuovere (deve essere in stato DISPONIBILE).
     * @throws IllegalStateException Se lo strumento selezionato è attualmente in prestito.
     * @author Lorenzo Manna
     */
    public void rimuoviStrumento(Strumento strumento) {
        synchronized (this) {
            if (strumento.getStato() == StatoStrumento.IN_PRESTITO) {
                throw new IllegalStateException("Impossibile rimuovere lo strumento " + strumento.getId() + ": è attualmente in prestito!");
            }
            catalogo.rimuoviStrumento(strumento);
            registraEvento("Strumento rimosso: " + strumento.getId() + " (" + strumento.getTipo() + ")");
        }
        notificaCambioStato();
    }

    /**
     * Imposta il listener che riceverà le notifiche sui cambiamenti di stato.
     * 
     * @param listener L'oggetto (generalmente la GUI) che implementa {@link ListenerLaboratorio}.
     * @author Lorenzo Manna
     */
    public void setListener(ListenerLaboratorio listener) {
        this.listener = listener;
    }
    
    /**
     * Notifica l'interfaccia grafica di un cambiamento di stato.
     * <p>
     * Utilizza {@code SwingUtilities.invokeLater()} per assicurarsi che 
     * l'aggiornamento della UI avvenga esclusivamente sull'Event Dispatch Thread (EDT).
     * </p>
     * 
     * @author Lorenzo Manna
     */
    private void notificaCambioStato() {
        if (listener != null) {
            javax.swing.SwingUtilities.invokeLater(() -> listener.statoCambiato());
        }
    }

    /**
     * Restituisce la lista di tutti gli strumenti presenti nel laboratorio.
     * 
     * @return Una vista in sola lettura della lista degli strumenti.
     * @author Simone Biagi
     */
    public synchronized List<Strumento> getTuttiStrumenti() {
        return catalogo.getTuttiStrumenti(); 
    }
    
    /**
     * Restituisce la lista degli strumenti attualmente assegnati a un utente.
     * 
     * @return La lista degli strumenti filtrati per stato IN_PRESTITO.
     * @author Simone Biagi
     */
    public synchronized List<Strumento> getStrumentiInPrestito(){
        return catalogo.getStrumentiInPrestito();
    }

    /**
     * Restituisce il conteggio degli strumenti liberi e disponibili.
     * 
     * @return Il numero di strumenti in stato DISPONIBILE.
     * @author Simone Biagi
     */
    public synchronized int getNumeroDisponibili() {
        return catalogo.getStrumentiDisponibili().size();
    }

    /**
     * Restituisce il conteggio degli strumenti attualmente assegnati.
     * 
     * @return Il numero di strumenti in stato IN_PRESTITO.
     * @author Simone Biagi
     */
    public synchronized int getNumeroInPrestito() {
        return catalogo.getStrumentiInPrestito().size();
    }

    /**
     * Aggiorna lo stato della richiesta per un determinato utente, oppure 
     * ne crea una nuova se l'utente non è ancora presente in lista.
     * 
     * @param utente Il nome dell'utente associato alla richiesta.
     * @param tipo   La categoria di strumento richiesta.
     * @param stato  Il nuovo stato da assegnare alla richiesta (es. IN_ATTESA).
     * @author Lorenzo Manna
     */
    private void aggiornaOCreaRichiesta(String utente, TipoStrumento tipo, StatoPrestito stato) {
        for (int i = 0; i < listaRichieste.size(); i++) {
            if (listaRichieste.get(i).getNomeUtente().equals(utente)) {
                // Sostituisce interamente l'oggetto per garantire thread-safety in caso di estrazione simultanea
                listaRichieste.set(i, new InfoRichiesta(utente, tipo, stato));
                return;
            }
        }
        listaRichieste.add(new InfoRichiesta(utente, tipo, stato));
    }
    
    /**
     * Sovrascrive completamente lo storico eventi con i dati caricati da file.
     * 
     * @param registroEventi La lista di eventi precedentemente salvati.
     * @author Lorenzo Manna
     */
    public void setRegistroEventi(List<EventLog> registroEventi) {
        synchronized (this) {
            this.registroEventi.clear();
            this.registroEventi.addAll(registroEventi);
        }
        notificaCambioStato();
    }
    
    /**
     * Ricostruisce l'intero catalogo basandosi su una lista di strumenti 
     * ricaricati dal salvataggio persistente.
     * 
     * @param catalogo Un oggetto Catalogo temporaneo ricostruito dal file.
     * @author Lorenzo Manna
     */
    public void setCatalogo(Catalogo catalogo) {
        synchronized (this) {
            this.catalogo.clear();
            for (Strumento strumento : catalogo.getTuttiStrumenti()) {
                this.catalogo.aggiungiStrumento(strumento);
            }
        }
        notificaCambioStato();
    }
}