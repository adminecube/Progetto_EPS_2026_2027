package it.unibs.eps.labmanager.simulation;

import java.util.Random;

import it.unibs.eps.labmanager.concurrency.MonitorLaboratorio;
import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.model.TipoStrumento;

public class UserThread implements Runnable{
	
	private static final int T_UTILIZZO_MAX = 4000;
	private static final int T_UTILIZZO_MIN = 1000;
	private final String nomeUtente;
	private final TipoStrumento tipoRichiesto;
	private final MonitorLaboratorio monitor;
	private final Random rnd;
	
	public UserThread(String nomeUtente,TipoStrumento tipoRichiesto, MonitorLaboratorio monitor) {
		this.nomeUtente = nomeUtente;
		this.tipoRichiesto = tipoRichiesto;
		this.monitor=monitor;
		this.rnd = new Random();
	}
	
	
	@Override
	public void run() {
		try {
			//L'utente richiede lo strumento (il monitor gestirà l'eventuale attesa)
			Strumento strumentoAssegnato = monitor.richiediStrumento(tipoRichiesto, nomeUtente);
			// Se il thread viene interrotto durante l'attesa, il monitor restituisce null
			if(strumentoAssegnato==null)
				return;
			//L'utente utilizza lo strumento
			//I tempi di simulazione rappresentano la durata di un prestito
			int tempoUtilizzo = T_UTILIZZO_MIN + rnd.nextInt(T_UTILIZZO_MAX-T_UTILIZZO_MIN);
			Thread.sleep(tempoUtilizzo);
			//L'utente restituisce lo strumento al laboratorio
			monitor.restituisciStrumento(strumentoAssegnato, nomeUtente);
			
		} catch (InterruptedException e) {
			// Se il thread viene interrotto durante lo sleep() (mentre ha lo strumento)
			// deve comunque segnalare la fine dell'esecuzione in modo pulito
			Thread.currentThread().interrupt();
		}
		
	}

}
