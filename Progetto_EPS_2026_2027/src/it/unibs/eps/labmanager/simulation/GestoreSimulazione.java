package it.unibs.eps.labmanager.simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unibs.eps.labmanager.concurrency.MonitorLaboratorio;
import it.unibs.eps.labmanager.model.TipoStrumento;

public class GestoreSimulazione {
	private final MonitorLaboratorio monitor;
	private ExecutorService executor;
	
	public GestoreSimulazione(MonitorLaboratorio monitor) {
		this.monitor=monitor;
	}
	
	public void avviaSim() {
		int nUsers = 6;
		executor = Executors.newFixedThreadPool(nUsers);
		
		//Nomi utenti e tipologie strumenti
		String[] nomi= {"Anna", "Luca", "Sara", "Eugenio", "Giulia", "Matteo"};
		//Simulo piu' richieste dello stesso tipo
		TipoStrumento[] tipo = {TipoStrumento.OSCILLOSCOPIO, TipoStrumento.OSCILLOSCOPIO,
								TipoStrumento.OSCILLOSCOPIO, TipoStrumento.GENERATORE,
								TipoStrumento.MULTIMETRO, TipoStrumento.MULTIMETRO};
		for(int i=0;i<nUsers;i++) {
			Runnable utente = new UserThread(nomi[i],tipo[i],monitor);
			executor.execute(utente);
		}
		
	}
	public void interrompiSimulazione() {
		// Invia un segnale di interrupt a tutti i thread in esecuzione o in attesa sul monitor	
		if(executor != null && !executor.isShutdown())
			executor.shutdownNow();
	}
}
