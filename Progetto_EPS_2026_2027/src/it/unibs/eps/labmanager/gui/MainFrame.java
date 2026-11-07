package it.unibs.eps.labmanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPane;
	private JTable tableRegistroEventi;
	
	// palette colori
	private Color colorBg = new Color(244, 245, 247);         
	private Color colorCard = new Color(255, 255, 255);        
	private Color colorBorder = new Color(226, 232, 240);     
	private Color colorTextDark = new Color(30, 41, 59);  
	private JTable tabStrumenti;
	private JTable tabRichieste;
	
	public MainFrame() {
		InitUI();
	}
	
	private void InitUI() {
		
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 550);
		setMinimumSize(new Dimension(750, 500));       
		
		mainPane = buildMainPanel();
		setContentPane(mainPane);
		
		JPanel panelSuperiore = buildUpperPanel();
		mainPane.add(panelSuperiore, BorderLayout.NORTH);
		
		JPanel panelCorpo = buildPanelCorpo();
		mainPane.add(panelCorpo, BorderLayout.CENTER);
		
		JPanel panelGestioneDati = buildPanelGestioneDati();
		mainPane.add(panelGestioneDati, BorderLayout.SOUTH);
	}

	private JPanel buildPanelCorpo() {
		JPanel panelCorpo = new JPanel();
		panelCorpo.setOpaque(false);
		panelCorpo.setLayout(new BorderLayout(0, 10));
		
		JPanel panelCentrale = buildPanelCentrale();
		panelCorpo.add(panelCentrale, BorderLayout.CENTER);
		
		JPanel panelRegistroEventi = buildPanelRegistroEventi();
		panelCorpo.add(panelRegistroEventi, BorderLayout.SOUTH);
		
		return panelCorpo;
	}

	private JPanel buildPanelRegistroEventi() {
		JPanel panelRegistroEventi = new JPanel();
		panelRegistroEventi.setBackground(colorCard);
		panelRegistroEventi.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(8, 10, 10, 10)
		));
		panelRegistroEventi.setLayout(new BorderLayout(0, 6));
		panelRegistroEventi.setPreferredSize(new Dimension(0, 170));
		
		JTextPane txtRegistroEventi = new JTextPane();
		txtRegistroEventi.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtRegistroEventi.setBackground(colorCard);
		txtRegistroEventi.setForeground(colorTextDark);
		txtRegistroEventi.setEditable(false);
		txtRegistroEventi.setText("Registro Eventi");
		panelRegistroEventi.add(txtRegistroEventi, BorderLayout.NORTH);
		
		tableRegistroEventi = new JTable();
		tableRegistroEventi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		tableRegistroEventi.setRowHeight(22);
		tableRegistroEventi.setGridColor(colorBorder);
		
		JScrollPane scrollTable = new JScrollPane(tableRegistroEventi);
		scrollTable.setBorder(new LineBorder(colorBorder, 1, true));
		panelRegistroEventi.add(scrollTable, BorderLayout.CENTER);
		
		return panelRegistroEventi;
	}

	private JPanel buildPanelCentrale() {
		JPanel panelCentrale = new JPanel();
		panelCentrale.setBackground(colorCard);
		panelCentrale.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(10, 10, 10, 10)
		));
		panelCentrale.setLayout(new GridLayout(1, 3, 10, 0));
		
		JPanel panelStrumenti = buildPanelStrumenti();
		panelCentrale.add(panelStrumenti);
		
		
		JPanel panelRichieste = buildPanelRichieste();
		panelCentrale.add(panelRichieste);
		
		JPanel panelSimulazione = buildPanelSimulazione();
		panelCentrale.add(panelSimulazione);
		
		return panelCentrale;
	}

	private JPanel buildPanelRichieste() {
		JPanel panelRichieste = new JPanel();
		panelRichieste.setBorder(new LineBorder(colorBorder, 1, true));
		panelRichieste.setBackground(colorBg);
		
		panelRichieste.setLayout(new BorderLayout(0, 0));
		
		JTextPane txtpnRichiesteConcorrenti = new JTextPane();
		txtpnRichiesteConcorrenti.setText("Richieste concorrenti");
		txtpnRichiesteConcorrenti.setForeground(new Color(30, 41, 59));
		txtpnRichiesteConcorrenti.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtpnRichiesteConcorrenti.setEditable(false);
		txtpnRichiesteConcorrenti.setBackground(new Color(244, 245, 247));
		panelRichieste.add(txtpnRichiesteConcorrenti, BorderLayout.NORTH);
		
		JScrollPane scrollPaneRichieste = new JScrollPane();
		panelRichieste.add(scrollPaneRichieste, BorderLayout.CENTER);
		scrollPaneRichieste.setPreferredSize(new Dimension(200, 120));
		
		tabRichieste = new JTable();
		scrollPaneRichieste.setViewportView(tabRichieste);
		
		return panelRichieste;
	}

	private JPanel buildPanelStrumenti() {
		JPanel panelStrumenti = new JPanel();
		panelStrumenti.setBorder(new LineBorder(colorBorder, 1, true));
		panelStrumenti.setBackground(colorBg);
		
		panelStrumenti.setLayout(new BorderLayout(0, 0));
		
		JTextPane txtStrumenti = new JTextPane();
		txtStrumenti.setText("Strumenti");
		txtStrumenti.setForeground(new Color(30, 41, 59));
		txtStrumenti.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtStrumenti.setEditable(false);
		txtStrumenti.setBackground(new Color(244, 245, 247));
		panelStrumenti.add(txtStrumenti, BorderLayout.NORTH);
		
		JScrollPane scrollPaneStrumenti = new JScrollPane();
		panelStrumenti.add(scrollPaneStrumenti, BorderLayout.CENTER);
		scrollPaneStrumenti.setPreferredSize(new Dimension(200, 120));;
		
		tabStrumenti = new JTable();
		scrollPaneStrumenti.setViewportView(tabStrumenti);
		
		return panelStrumenti;
	}

	private JPanel buildPanelSimulazione() {
		JPanel panelSimulazione = new JPanel();
		panelSimulazione.setBorder(new LineBorder(colorBorder, 1, true));
		panelSimulazione.setBackground(colorBg);
		
		panelSimulazione.setLayout(new GridLayout(0, 1, 0, 0));
		
		JTextPane txtpnSimulazione = new JTextPane();
		txtpnSimulazione.setText("Simulazione");
		txtpnSimulazione.setForeground(new Color(30, 41, 59));
		txtpnSimulazione.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtpnSimulazione.setEditable(false);
		txtpnSimulazione.setBackground(new Color(244, 245, 247));
		panelSimulazione.add(txtpnSimulazione);
		
		JTextPane txtStatoMonitor = new JTextPane();
		txtStatoMonitor.setText("Stato Monitor:");
		txtStatoMonitor.setForeground(new Color(30, 41, 59));
		txtStatoMonitor.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtStatoMonitor.setEditable(false);
		txtStatoMonitor.setBackground(new Color(244, 245, 247));
		panelSimulazione.add(txtStatoMonitor);
		
		JButton btnAvvioSimulazione = new JButton("Avvio Simulazione");
		btnAvvioSimulazione.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnAvvioSimulazione.setForeground(colorTextDark);
		panelSimulazione.add(btnAvvioSimulazione);
		
		JButton btnInterrompi = new JButton("Interrompi");
		btnInterrompi.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnInterrompi.setForeground(colorTextDark);
		panelSimulazione.add(btnInterrompi);
		
		return panelSimulazione;
	}

	private JPanel buildPanelGestioneDati() {
		JPanel panelGestioneDati = new JPanel();
		panelGestioneDati.setBackground(colorCard);
		panelGestioneDati.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(4, 10, 4, 10)
		));
		panelGestioneDati.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 2));
		
		JButton saveButton = new JButton("Salva dati");
		saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		saveButton.setPreferredSize(new Dimension(100, 26));
		panelGestioneDati.add(saveButton);
		
		JButton loadButton = new JButton("Carica dati");
		loadButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		loadButton.setPreferredSize(new Dimension(100, 26));
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelGestioneDati.add(loadButton);
		
		return panelGestioneDati;
	}

	private JPanel buildUpperPanel() {
		JPanel panelSuperiore = new JPanel();
		panelSuperiore.setBackground(colorCard);
		panelSuperiore.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(10, 10, 10, 10)
		));
		// Definisce l'altezza preferita a 85px (in BorderLayout.NORTH la larghezza viene adattata automaticamente)
		panelSuperiore.setPreferredSize(new Dimension(0, 85));
		panelSuperiore.setLayout(new GridLayout(1, 3, 10, 0));
		
		JPanel panelDisponibili = new JPanel();
		panelDisponibili.setBackground(colorBg);
		panelDisponibili.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(6, 10, 6, 10)
		));
		panelDisponibili.setLayout(new BorderLayout(0, 0));
		panelSuperiore.add(panelDisponibili);
		
		JTextPane txtpnStrumentiDisponibili = new JTextPane();
		txtpnStrumentiDisponibili.setText("Strumenti disponibili: ");
		txtpnStrumentiDisponibili.setForeground(new Color(30, 41, 59));
		txtpnStrumentiDisponibili.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtpnStrumentiDisponibili.setEditable(false);
		txtpnStrumentiDisponibili.setBackground(new Color(244, 245, 247));
		panelDisponibili.add(txtpnStrumentiDisponibili, BorderLayout.NORTH);
		
		JPanel panelPrestito = new JPanel();
		panelPrestito.setBackground(colorBg);
		panelPrestito.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(6, 10, 6, 10)
		));
		panelPrestito.setLayout(new BorderLayout(0, 0));
		panelSuperiore.add(panelPrestito);
		
		JTextPane txtpnStrumenti = new JTextPane();
		txtpnStrumenti.setText("Strumenti in prestito:");
		txtpnStrumenti.setForeground(new Color(30, 41, 59));
		txtpnStrumenti.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtpnStrumenti.setEditable(false);
		txtpnStrumenti.setBackground(new Color(244, 245, 247));
		panelPrestito.add(txtpnStrumenti, BorderLayout.NORTH);
		
		JPanel panelAttesa = new JPanel();
		panelAttesa.setBackground(colorBg);
		panelAttesa.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(6, 10, 6, 10)
		));
		panelAttesa.setLayout(new BorderLayout(0, 0));
		panelSuperiore.add(panelAttesa);
		
		JTextPane txtpnUtentiInAttesa = new JTextPane();
		txtpnUtentiInAttesa.setText("Utenti in attesa:");
		txtpnUtentiInAttesa.setForeground(new Color(30, 41, 59));
		txtpnUtentiInAttesa.setFont(new Font("Segoe UI", Font.BOLD, 14));
		txtpnUtentiInAttesa.setEditable(false);
		txtpnUtentiInAttesa.setBackground(new Color(244, 245, 247));
		panelAttesa.add(txtpnUtentiInAttesa, BorderLayout.NORTH);
		
		return panelSuperiore;
	}

	private JPanel buildMainPanel() {
		JPanel pannelloPrincipale = new JPanel();
		pannelloPrincipale.setBackground(colorBg);
		pannelloPrincipale.setBorder(new EmptyBorder(12, 12, 12, 12));
		pannelloPrincipale.setLayout(new BorderLayout(0, 10));
		
		return pannelloPrincipale;
	}
}
