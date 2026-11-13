package it.unibs.eps.labmanager.main;

import javax.swing.SwingUtilities;

import it.unibs.eps.labmanager.concurrency.MonitorLaboratorio;
import it.unibs.eps.labmanager.gui.MainFrame;
import it.unibs.eps.labmanager.model.Catalogo;
import it.unibs.eps.labmanager.simulation.GestoreSimulazione;

/**
 * Punto d'ingresso principale dell'applicazione LabManager.
 * Inizializza e collega tra loro i livelli del sistema (Model, Monitor, Simulation e GUI).
 */
public class Main {

	/**
	 * Avvia l'applicazione garantendo che l'interfaccia grafica venga creata
	 * e gestita sull'Event Dispatch Thread (EDT) di Swing.
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			
			Catalogo catalogo = new Catalogo();

			MonitorLaboratorio monitor = new MonitorLaboratorio(catalogo);

			GestoreSimulazione gs = new GestoreSimulazione(monitor);

			MainFrame frame = new MainFrame(monitor, gs);
			frame.setVisible(true);
		});
	}
}