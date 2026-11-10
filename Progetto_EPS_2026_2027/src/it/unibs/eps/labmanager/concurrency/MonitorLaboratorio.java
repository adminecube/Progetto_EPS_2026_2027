package it.unibs.eps.labmanager.concurrency;
import it.unibs.eps.labmanager.model.*;

import java.util.ArrayList;
import java.util.List;

public class MonitorLaboratorio {
	//Lista contenente gli strumenti
	private final Catalogo catalogo;
	private final List<EventLog> registroEventi; // Storico consultabile
	
	private ListenerLaboratorio listener;
	
	//Costruttore
	public MonitorLaboratorio(Catalogo catalogo) {
		this.catalogo=catalogo;
		this.registroEventi = new ArrayList<>();
	}
	
	public synchronized Strumento richiediStrumento(TipoStrumento tipo,String nomeUtente) {
		Strumento strumentoTrovato = catalogo.trovaStrumentoDisponibile(tipo);
		
		// Se lo strumento non � subito disponibile, si registra l'attesa una sola volta
		if(strumentoTrovato==null)
			registraEvento(nomeUtente + " in attesa di "+ tipo);
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
        return strumentoTrovato;
	}
	/*
     * Metodo privato per aggiungere un log in tempo reale.
     * � sicuro perch� richiamato solo dall'interno di metodi synchronized.
     */
	private void registraEvento(String descrizione) {
		registroEventi.add(new EventLog(descrizione));
    }
	/*
     * Restituisce uno strumento al laboratorio e risveglia i thread in attesa.
     */
    public synchronized void restituisciStrumento(Strumento strumento, String nomeUtente) {
    	strumento.setStato(StatoStrumento.DISPONIBILE);
        registraEvento(nomeUtente + " restituisce " + strumento.getId());
        // La restituzione deve rendere possibile il progresso delle richieste in attesa
        notifyAll(); 
    }
    /*
     * Restituisce una copia dello storico per visualizzarlo o salvarlo.
     * � synchronized per evitare letture inconsistenti durante la scrittura.
     */
    public synchronized List<EventLog> getRegistroEventi() {
        return new ArrayList<>(registroEventi); 
    }
    
    public synchronized void aggiungiStrumento(Strumento strumento) {
    	catalogo.aggiungiStrumento(strumento);
        registraEvento("Nuovo strumento aggiunto: " + strumento.getId() + " (" + strumento.getTipo() + ")");
        
        // Risveglia eventuali thread che attendevano uno strumento di questo tipo
        notifyAll();
        
        // Aggiorna l'interfaccia grafica
        notificaCambioStato();
    }
    
    public void setListener(ListenerLaboratorio listener) {
    	this.listener = listener;
    }
    
    private void notificaCambioStato() {
    	if (listener != null) {
            javax.swing.SwingUtilities.invokeLater(() -> listener.statoCambiato());
        }
    }

	public Catalogo getCatalogo() {
		return catalogo;
	}
    
}
