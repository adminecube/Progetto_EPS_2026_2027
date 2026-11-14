package it.unibs.eps.labmanager.simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unibs.eps.labmanager.concurrency.MonitorLaboratorio;
import it.unibs.eps.labmanager.model.TipoStrumento;

/**
 * Gestisce il ciclo di vita della simulazione degli accessi concorrenti al laboratorio.
 * <p>
 * Utilizza un {@link ExecutorService} per istanziare e orchestrare un pool di thread,
 * ciascuno dei quali rappresenta un utente che tenta di prendere in prestito uno strumento.
 * Consente di avviare la simulazione e di interromperla forzatamente inviando segnali 
 * di interrupt ai thread in esecuzione o in attesa.
 * </p>
 */
public class GestoreSimulazione {
	private final MonitorLaboratorio monitor;
	private ExecutorService executor;
	
    /**
     * Costruisce il gestore della simulazione.
     *
     * @param monitor L'oggetto monitor condiviso su cui i thread della simulazione 
     *                effettueranno le richieste e le restituzioni.
     */
	public GestoreSimulazione(MonitorLaboratorio monitor) {
		this.monitor=monitor;
	}
	
    /**
     * Avvia una nuova sessione di simulazione.
     * <p>
     * Crea un Thread Pool di dimensioni fisse e sottomette un numero predefinito 
     * di task ({@link UserThread}). Ogni task simulerà un utente che richiede 
     * una specifica tipologia di strumento. 
     * Dopo la sottomissione, viene invocato {@code shutdown()} per impedire 
     * l'aggiunta di nuovi task, lasciando terminare quelli correnti.
     * </p>
     */
	public void avviaSim() {
		int nUsers = 6;
		executor = Executors.newFixedThreadPool(nUsers);
		
		// Nomi utenti e tipologie strumenti
		String[] nomi= {"Anna", "Luca", "Sara", "Eugenio", "Giulia", "Matteo"};
		// Simulo piu' richieste dello stesso tipo
		TipoStrumento[] tipo = {TipoStrumento.OSCILLOSCOPIO, TipoStrumento.OSCILLOSCOPIO,
								TipoStrumento.OSCILLOSCOPIO, TipoStrumento.GENERATORE,
								TipoStrumento.MULTIMETRO, TipoStrumento.MULTIMETRO};
		for(int i=0;i<nUsers;i++) {
			Runnable utente = new UserThread(nomi[i],tipo[i],monitor);
			executor.execute(utente);
		}
		
		executor.shutdown();
	}
	
    /**
     * Interrompe la simulazione in corso.
     * <p>
     * Invia un segnale di interrupt (tramite {@code shutdownNow()}) a tutti i 
     * thread gestiti dall'executor. I thread bloccati in attesa sul monitor 
     * (in fase di wait) o in utilizzo (in fase di sleep) cattureranno l'eccezione 
     * e termineranno la loro esecuzione in modo pulito.
     * </p>
     */
	public void interrompiSimulazione() {
		// Invia un segnale di interrupt a tutti i thread in esecuzione o in attesa sul monitor	
		if(executor != null && !executor.isTerminated())
			executor.shutdownNow();
	}
	
    /**
     * Verifica se la simulazione è attualmente in esecuzione.
     *
     * @return {@code true} se ci sono thread della simulazione attivi o in attesa, 
     *         {@code false} se la simulazione è terminata o non è mai stata avviata.
     */
	public boolean isRunning() {
		return executor != null && !executor.isTerminated();
	}
}