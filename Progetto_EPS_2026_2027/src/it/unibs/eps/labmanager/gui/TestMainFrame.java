package it.unibs.eps.labmanager.gui;

import it.unibs.eps.labmanager.concurrency.MonitorLaboratorio;
import it.unibs.eps.labmanager.model.Catalogo;
import it.unibs.eps.labmanager.simulation.GestoreSimulazione;

public class TestMainFrame {

	public static void main(String[] args) {
		
		Catalogo catalogo = new Catalogo();
		MonitorLaboratorio monitor = new MonitorLaboratorio(catalogo);
		GestoreSimulazione gs = new GestoreSimulazione(monitor);
		MainFrame frame = new MainFrame(monitor, gs);
		frame.setVisible(true);

	}

}
