package it.unibs.eps.labmanager.simulation;

import java.util.Random;

import it.unibs.eps.labmanager.concurrency.MonitorLaboratorio;
import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.model.TipoStrumento;

/**
 * Rappresenta un singolo utente del laboratorio sotto forma di thread indipendente.
 * <p>
 * Implementa {@link Runnable} per simulare il comportamento di un utente che:
 * 1. Richiede uno strumento al {@link MonitorLaboratorio} (potenzialmente mettendosi in attesa).
 * 2. Utilizza lo strumento per un tempo casuale (simulando la durata del prestito tramite sleep).
 * 3. Restituisce in modo sicuro lo strumento, anche in caso di interruzioni impreviste.
 * </p>
 */
public class UserThread implements Runnable{
	
	private static final int T_UTILIZZO_MAX = 4000;
	private static final int T_UTILIZZO_MIN = 1000;
	
	private final String nomeUtente;
	private final TipoStrumento tipoRichiesto;
	private final MonitorLaboratorio monitor;
	private final Random rnd;
	
    /**
     * Crea un nuovo thread utente.
     *
     * @param nomeUtente    Il nome dell'utente (usato per i log e la UI).
     * @param tipoRichiesto La categoria di strumento di cui l'utente ha bisogno.
     * @param monitor       Il riferimento all'oggetto monitor condiviso per la sincronizzazione.
     */
	public UserThread(String nomeUtente,TipoStrumento tipoRichiesto, MonitorLaboratorio monitor) {
		this.nomeUtente = nomeUtente;
		this.tipoRichiesto = tipoRichiesto;
		this.monitor=monitor;
		this.rnd = new Random();
	}
	
    /**
     * Contiene la logica di esecuzione concorrente dell'utente.
     * Gestisce l'acquisizione, l'uso simulato e il rilascio dello strumento, 
     * garantendo sempre la coerenza dello stato del laboratorio attraverso un blocco finally.
     */
	@Override
	public void run() {
		
		Strumento strumentoAssegnato = null;
		
		try {
			// L'utente richiede lo strumento (il monitor gestirà l'eventuale attesa)
			strumentoAssegnato = monitor.richiediStrumento(tipoRichiesto, nomeUtente);
			
			// Se il thread viene interrotto durante l'attesa, il monitor restituisce null
			if(strumentoAssegnato==null)
				return;
			
			// L'utente utilizza lo strumento
			// I tempi di simulazione rappresentano la durata di un prestito
			int tempoUtilizzo = T_UTILIZZO_MIN + rnd.nextInt(T_UTILIZZO_MAX-T_UTILIZZO_MIN);
			Thread.sleep(tempoUtilizzo);
			
		} catch (InterruptedException e) {
			// Se il thread viene interrotto durante lo sleep() (mentre ha lo strumento)
			// deve comunque segnalare la fine dell'esecuzione in modo pulito ripristinando il flag
			Thread.currentThread().interrupt();
		} finally {
		    // Eseguito sempre, garantisce il rilascio del lock e la restituzione della risorsa.
		    // La restituzione avviene solo se la risorsa è stata effettivamente acquisita.
		    if (strumentoAssegnato != null) {
		        monitor.restituisciStrumento(strumentoAssegnato, nomeUtente);
		    }
		}
	}
}