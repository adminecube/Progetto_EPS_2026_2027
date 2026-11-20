package it.unibs.eps.labmanager.concurrency;

/**
 * Interfaccia funzionale per l'implementazione del pattern Observer.
 * <p>
 * Consente al {@link MonitorLaboratorio} di notificare componenti esterni 
 * (come l'interfaccia grafica) ogni volta che avviene una modifica allo stato 
 * del sistema, garantendo il disaccoppiamento tra la logica di concorrenza e la GUI.
 * </p>
 * @author Lorenzo Manna
 */
public interface ListenerLaboratorio {

    /**
     * Metodo di callback invocato quando lo stato del laboratorio 
     * (strumenti, richieste o registro eventi) subisce una variazione.
     */
	public void statoCambiato();
	
}