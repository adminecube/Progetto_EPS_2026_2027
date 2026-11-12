package it.unibs.eps.labmanager.concurrency;
import it.unibs.eps.labmanager.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lorenzo Manna
 * @author Simone Biagi
 */

public class MonitorLaboratorio {
	//Lista contenente gli strumenti
	private final Catalogo catalogo;
	private final List<EventLog> registroEventi; // Storico consultabile
	private final List<InfoRichiesta> listaRichieste;
	
	private ListenerLaboratorio listener;
	
	//Costruttore
	/**
	 * @author Simone Biagi
	 * @param catalogo
	 */
	public MonitorLaboratorio(Catalogo catalogo) {
		this.catalogo=catalogo;
		this.registroEventi = new ArrayList<>();
		this.listaRichieste = new ArrayList<>();
	}
	
	
	/**
	 * 
	 * @param tipo
	 * @param nomeUtente
	 * @return
	 * @author Simone Biagi
	 * @author Lorenzo Manna
	 */
	public synchronized Strumento richiediStrumento(TipoStrumento tipo,String nomeUtente) {
		Strumento strumentoTrovato = catalogo.trovaStrumentoDisponibile(tipo);
		
		// Se lo strumento non � subito disponibile, si registra l'attesa una sola volta
		if(strumentoTrovato==null) {
			registraEvento(nomeUtente + " in attesa di "+ tipo);
			aggiornaOCreaRichiesta(nomeUtente, tipo, StatoPrestito.IN_ATTESA);
			notificaCambioStato();
		}
		// Un thread in attesa deve ricontrollare la condizione al risveglio
		while(strumentoTrovato==null) {
			try {
				// Mette in attesa la richiesta quando tutti gli strumenti del tipo richiesto sono occupati
				wait();
			} catch (InterruptedException e) {
				// L'interruzione viene gestita senza lasciare lo stato del laboratorio incoerente
				Thread.currentThread().interrupt();
				return null;
			}
			// Al risveglio, delega nuovamente la ricerca al Catalogo
			strumentoTrovato = catalogo.trovaStrumentoDisponibile(tipo);
		}
		// Il controllo della disponibilit� e l'assegnazione avvengono come un'unica operazione protetta
		strumentoTrovato.setStato(StatoStrumento.IN_PRESTITO);
        registraEvento(nomeUtente + " ottiene " + strumentoTrovato.getId());
        aggiornaOCreaRichiesta(nomeUtente, tipo, StatoPrestito.IN_PRESTITO);
        notificaCambioStato();
        return strumentoTrovato;
	}
	/**
	 * @author Simone Biagi
     * Metodo privato per aggiungere un log in tempo reale.
     * � sicuro perch� richiamato solo dall'interno di metodi synchronized.
     */
	private void registraEvento(String descrizione) {
		registroEventi.add(new EventLog(descrizione));
    }
	/*
     * Restituisce uno strumento al laboratorio e risveglia i thread in attesa.
     */
	
	/**
	 * 
	 * @param strumento
	 * @param nomeUtente
	 * @author Simone Biagi
	 * @author Lorenzo Manna
	 */
    public synchronized void restituisciStrumento(Strumento strumento, String nomeUtente) {
    	strumento.setStato(StatoStrumento.DISPONIBILE);
        registraEvento(nomeUtente + " restituisce " + strumento.getId());
        aggiornaOCreaRichiesta(nomeUtente, strumento.getTipo(), StatoPrestito.RESTITUITO);
        notificaCambioStato();
        // La restituzione deve rendere possibile il progresso delle richieste in attesa
        notifyAll(); 
    }
    /**
     * @author Simone Biagi
     * Restituisce una copia dello storico per visualizzarlo o salvarlo.
     * � synchronized per evitare letture inconsistenti durante la scrittura.
     */
    public synchronized List<EventLog> getRegistroEventi() {
        return List.copyOf(registroEventi); 
    }
    
    
    public synchronized List<InfoRichiesta> getListaRichieste() {
        return List.copyOf(listaRichieste); 
    }
    /**
     * @author Lorenzo Manna
     * @param strumento
     */
    public synchronized void aggiungiStrumento(Strumento strumento) {
    	catalogo.aggiungiStrumento(strumento);
        registraEvento("Nuovo strumento aggiunto: " + strumento.getId() + " (" + strumento.getTipo() + ")");
        
        // Risveglia eventuali thread che attendevano uno strumento di questo tipo
        notifyAll();
        
        // Aggiorna l'interfaccia grafica
        notificaCambioStato();
    }
    
    public synchronized void rimuoviStrumento(Strumento strumento) {
    	if (strumento.getStato() == StatoStrumento.IN_PRESTITO) {
            throw new IllegalStateException("Impossibile rimuovere lo strumento " + strumento.getId() + ": è attualmente in prestito!");
        }
    	catalogo.rimuoviStrumento(strumento);
    	registraEvento("Strumento rimosso: " + strumento.getId() + " (" + strumento.getTipo() + ")");
    	notificaCambioStato();
    }
    /**
     * @author Lorenzo Manna
     * @param listener
     */
    public void setListener(ListenerLaboratorio listener) {
    	this.listener = listener;
    }
    
    /**
     * @author Lorenzo Manna
     */
    private void notificaCambioStato() {
    	if (listener != null) {
            javax.swing.SwingUtilities.invokeLater(() -> listener.statoCambiato());
        }
    }
    /**
     * @author Simone Biagi
     * @return
     */

	public Catalogo getCatalogo() {
		return catalogo;
	}
	/**
	 * @author Lorenzo Manna
	 * @param utente
	 * @param tipo
	 * @param stato
	 */
	private void aggiornaOCreaRichiesta(String utente, TipoStrumento tipo, StatoPrestito stato) {
        for (InfoRichiesta r : listaRichieste) {
            if (r.getNomeUtente().equals(utente)) {
                r.setStato(stato);
                return;
            }
        }
        InfoRichiesta nuova = new InfoRichiesta(utente, tipo, stato);
        listaRichieste.add(nuova);
    }
	
    
}
