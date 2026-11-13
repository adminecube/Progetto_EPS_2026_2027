package it.unibs.eps.labmanager.concurrency;

import it.unibs.eps.labmanager.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lorenzo Manna
 * @author Simone Biagi
 */
public class MonitorLaboratorio {
    private final Catalogo catalogo;
    private final List<EventLog> registroEventi; // Storico consultabile
    private final List<InfoRichiesta> listaRichieste;
    
    private ListenerLaboratorio listener;
    
    /**
     * @author Simone Biagi
     * @param catalogo Il catalogo di partenza
     */
    public MonitorLaboratorio(Catalogo catalogo) {
        this.catalogo = catalogo;
        this.registroEventi = new ArrayList<>();
        this.listaRichieste = new ArrayList<>();
    }
    
    /**
     * @author Simone Biagi
     * @param tipo Tipo di strumento richiesto
     * @param nomeUtente Nome dell'utente richiedente
     * @return Lo strumento assegnato, oppure null se fallito/rifiutato
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
        
        // Chiamata all'interfaccia FUORI dal blocco synchronized
        notificaCambioStato(); 
        return assegnato;
    }

    /**
     * @author Simone Biagi
     * Metodo privato per aggiungere un log in tempo reale.
     * È sicuro perché richiamato solo dall'interno di blocchi synchronized.
     */
    private void registraEvento(String descrizione) {
        registroEventi.add(new EventLog(descrizione));
    }
    
    /**
     * @author Simone Biagi
     * Restituisce uno strumento al laboratorio e risveglia i thread in attesa.
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
     * @author Simone Biagi
     * Restituisce una copia dello storico per visualizzarlo o salvarlo.
     */
    public synchronized List<EventLog> getRegistroEventi() {
        return List.copyOf(registroEventi); 
    }
    
    /**
     * Restituisce una Deep Copy della lista per evitare modifiche accidentali o da concorrenza
     */
    public synchronized List<InfoRichiesta> getListaRichieste() {
        List<InfoRichiesta> copia = new ArrayList<>();
        for (InfoRichiesta r : listaRichieste) {
            copia.add(new InfoRichiesta(r.getNomeUtente(), r.getTipoStrumento(), r.getStato()));
        }
        return copia; 
    }

    /**
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
     * @author Lorenzo Manna
     */
    public void setListener(ListenerLaboratorio listener) {
        this.listener = listener;
    }
    
    /**
     * @author Lorenzo Manna
     * Aggiorna la GUI tramite SwingUtilities (Non blocca mai il Monitor)
     */
    private void notificaCambioStato() {
        if (listener != null) {
            javax.swing.SwingUtilities.invokeLater(() -> listener.statoCambiato());
        }
    }

    /**
     * @author Simone Biagi
     */
    public synchronized List<Strumento> getTuttiStrumenti() {
        return catalogo.getTuttiStrumenti(); 
    }
    
    /**
     * @author Simone Biagi
     */
    public synchronized List<Strumento> getStrumentiInPrestito(){
        return catalogo.getStrumentiInPrestito();
    }

    /**
     * @author Simone Biagi
     */
    public synchronized int getNumeroDisponibili() {
        return catalogo.getStrumentiDisponibili().size();
    }

    /**
     * @author Simone Biagi
     */
    public synchronized int getNumeroInPrestito() {
        return catalogo.getStrumentiInPrestito().size();
    }

    /**
     * @author Lorenzo Manna
     * Metodo di servizio (da chiamare all'interno di blocchi synchronized)
     * Crea un nuovo oggetto per la sovrascrittura in lista garantendo thread-safety
     */
    private void aggiornaOCreaRichiesta(String utente, TipoStrumento tipo, StatoPrestito stato) {
        for (int i = 0; i < listaRichieste.size(); i++) {
            if (listaRichieste.get(i).getNomeUtente().equals(utente)) {
                listaRichieste.set(i, new InfoRichiesta(utente, tipo, stato));
                return;
            }
        }
        listaRichieste.add(new InfoRichiesta(utente, tipo, stato));
    }
    
    /**
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
