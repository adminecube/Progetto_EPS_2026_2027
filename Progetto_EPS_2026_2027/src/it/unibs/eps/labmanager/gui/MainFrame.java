package it.unibs.eps.labmanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import it.unibs.eps.labmanager.concurrency.MonitorLaboratorio;
import it.unibs.eps.labmanager.model.EventLog;
import it.unibs.eps.labmanager.model.InfoRichiesta;
import it.unibs.eps.labmanager.model.StatoPrestito;
import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.simulation.GestoreSimulazione;

/**
 * @author Lorenzo Manna
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPane;
	
	private GestoreSimulazione gestoreSimulazione;
	private MonitorLaboratorio monitor;
	
	// Palette colori moderna
	private Color colorBg = new Color(244, 245, 247);         
	private Color colorCard = new Color(255, 255, 255);        
	private Color colorBorder = new Color(226, 232, 240);     
	private Color colorTextDark = new Color(30, 41, 59);
	private Color colorHeaderBg = new Color(241, 245, 249);
	private Color colorSelection = new Color(224, 231, 255);
	
	// Tabelle
	private JTable tabStrumenti;
	private JTable tabRichieste;
	private JTable tableRegistroEventi;
	
	// Modelli delle tabelle estratti come campi di classe
	private DefaultTableModel modelStrumenti;
	private DefaultTableModel modelRichieste;
	private DefaultTableModel modelRegistroEventi;
	
	// Pulsanti
	private JButton btnInterrompi;
	private JButton btnAvvioSimulazione;
	private JButton btnAggiungiStrumento;
	private JButton btnRimuoviStrumento;
	
	// Etichette dell'UpperPanel
	private JLabel lblValoreDisponibili;
	private JLabel lblValorePrestito;
	private JLabel lblValoreAttesa;
	
	private FrameCreaStrumento frameCreaStrumento;
	private FrameRimuoviStrumento frameRimuoviStrumento;
	
	public MainFrame(MonitorLaboratorio monitor, GestoreSimulazione gestoreSimulazione) {
		this.frameCreaStrumento = new FrameCreaStrumento((strumento) -> monitor.aggiungiStrumento(strumento));
		this.frameRimuoviStrumento = new FrameRimuoviStrumento((strumento) -> monitor.rimuoviStrumento(strumento));
		this.monitor = monitor;
		this.gestoreSimulazione = gestoreSimulazione;
		
		monitor.setListener(() -> aggiornaGUI());
		
		InitUI();
		setBtnListeners();
		aggiornaGUI();
	}
	
	public void aggiornaGUI() {
		aggiornaTabStrumenti();
		aggiornaTabRegistro();
		aggiornaUpperPanel();
		aggiornaTabRichieste();
	}
	
	public void aggiornaTabStrumenti() {
		if (monitor == null || modelStrumenti == null) return;
		modelStrumenti.setRowCount(0);
		
		for (Strumento s : monitor.getCatalogo().getTuttiStrumenti()) {
			modelStrumenti.addRow(new Object[]{
				s.getTipo(),
				s.getId(),
				s.getStato()
			});
		}
	}
	
	public void aggiornaTabRegistro() {
		if (monitor == null || modelRegistroEventi == null) return;
		modelRegistroEventi.setRowCount(0);
			
		for (EventLog event : monitor.getRegistroEventi()) {
			modelRegistroEventi.addRow(new Object[]{
				event.getOrarioFormattato(),
				event.getDescrizione()
			});
		}
	}
	
	public void aggiornaTabRichieste() {
		if (monitor == null || modelRichieste == null) return;
		modelRichieste.setRowCount(0);
			
		for (InfoRichiesta richiesta : monitor.getListaRichieste()) {
			modelRichieste.addRow(new Object[]{
				richiesta.getNomeUtente(),
				richiesta.getTipoStrumento(),
				richiesta.getStato().getNomeDescrittivo()
			});
		}
	}
	
	public void aggiornaUpperPanel() {
		if (monitor == null) return;
		int numeroStrumentiDisponibili = monitor.getCatalogo().getStrumentiDisponibili().size();
		int numeroStrumentiInPrestito = monitor.getCatalogo().getStrumentiInPrestito().size();
		long utentiInAttesa = monitor.getListaRichieste().stream()
				.filter(r -> r.getStato() == StatoPrestito.IN_ATTESA)
				.count();
		
		lblValoreDisponibili.setText(Integer.toString(numeroStrumentiDisponibili));
		lblValorePrestito.setText(Integer.toString(numeroStrumentiInPrestito));
		lblValoreAttesa.setText(Long.toString(utentiInAttesa));
	}
	
	private void setBtnListeners() {
		btnAvvioSimulazione.addActionListener(e -> gestoreSimulazione.avviaSim());
		btnInterrompi.addActionListener(e -> gestoreSimulazione.interrompiSimulazione());
		btnAggiungiStrumento.addActionListener(e -> frameCreaStrumento.setVisible(true));
		btnRimuoviStrumento.addActionListener(e -> {frameRimuoviStrumento.aggiornaListaStrumenti(monitor.getCatalogo().getTuttiStrumenti());
												    frameRimuoviStrumento.setVisible(true);});
	}
	
	private void InitUI() {
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 580);
		setMinimumSize(new Dimension(800, 520));       
		
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
		panelRegistroEventi.setPreferredSize(new Dimension(0, 180));
		
		JLabel lblRegistroEventi = new JLabel("Registro Eventi");
		lblRegistroEventi.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblRegistroEventi.setForeground(colorTextDark);
		lblRegistroEventi.setBorder(new EmptyBorder(2, 4, 4, 4));
		panelRegistroEventi.add(lblRegistroEventi, BorderLayout.NORTH);
		
		modelRegistroEventi = buildTableREModel();
		tableRegistroEventi = new JTable(modelRegistroEventi);
		stilaTabella(tableRegistroEventi);
		
		// Centra la colonna dell'Orario
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		tableRegistroEventi.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		tableRegistroEventi.getColumnModel().getColumn(0).setPreferredWidth(90);
		tableRegistroEventi.getColumnModel().getColumn(0).setMaxWidth(120);
		
		JScrollPane scrollTable = new JScrollPane(tableRegistroEventi);
		scrollTable.setBorder(new LineBorder(colorBorder, 1, true));
		scrollTable.getViewport().setBackground(colorCard);
		panelRegistroEventi.add(scrollTable, BorderLayout.CENTER);
		
		return panelRegistroEventi;
	}
	
	private DefaultTableModel buildTableREModel() {
		String[] intestazione = {"Orario", "Evento"};
		return new DefaultTableModel(intestazione, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
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
		
		JLabel lblRichieste = new JLabel("Richieste concorrenti");
		lblRichieste.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblRichieste.setForeground(colorTextDark);
		lblRichieste.setBorder(new EmptyBorder(6, 8, 6, 8));
		panelRichieste.add(lblRichieste, BorderLayout.NORTH);
		
		modelRichieste = buildTableRichiesteModel();
		tabRichieste = new JTable(modelRichieste);
		stilaTabella(tabRichieste);
		
		JScrollPane scrollPaneRichieste = new JScrollPane(tabRichieste);
		scrollPaneRichieste.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, colorBorder));
		scrollPaneRichieste.getViewport().setBackground(colorCard);
		panelRichieste.add(scrollPaneRichieste, BorderLayout.CENTER);
		
		return panelRichieste;
	}
	
	private DefaultTableModel buildTableRichiesteModel() {
		String[] intestazione = {"Utente", "Strumento", "Stato"};
		return new DefaultTableModel(intestazione, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	private JPanel buildPanelStrumenti() {
		JPanel panelStrumenti = new JPanel();
		panelStrumenti.setBorder(new LineBorder(colorBorder, 1, true));
		panelStrumenti.setBackground(colorBg);
		panelStrumenti.setLayout(new BorderLayout(0, 0));
		
		JLabel lblStrumenti = new JLabel("Strumenti");
		lblStrumenti.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblStrumenti.setForeground(colorTextDark);
		lblStrumenti.setBorder(new EmptyBorder(6, 8, 6, 8));
		panelStrumenti.add(lblStrumenti, BorderLayout.NORTH);
		
		modelStrumenti = buildTableStrumentiModel();
		tabStrumenti = new JTable(modelStrumenti);
		stilaTabella(tabStrumenti);
		
		// Centra la colonna Codice
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		tabStrumenti.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		
		JScrollPane scrollPaneStrumenti = new JScrollPane(tabStrumenti);
		scrollPaneStrumenti.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, colorBorder));
		scrollPaneStrumenti.getViewport().setBackground(colorCard);
		panelStrumenti.add(scrollPaneStrumenti, BorderLayout.CENTER);
		
		return panelStrumenti;
	}
	
	private DefaultTableModel buildTableStrumentiModel() {
		String[] intestazione = {"Nome", "Codice", "Stato"};
		return new DefaultTableModel(intestazione, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	private JPanel buildPanelSimulazione() {
		JPanel panelSimulazione = new JPanel();
		panelSimulazione.setBorder(new LineBorder(colorBorder, 1, true));
		panelSimulazione.setBackground(colorBg);
		panelSimulazione.setLayout(new GridLayout(0, 1, 6, 6));
		panelSimulazione.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(10, 10, 10, 10)
		));
		
		JLabel lblSimulazione = new JLabel("Simulazione");
		lblSimulazione.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblSimulazione.setForeground(colorTextDark);
		panelSimulazione.add(lblSimulazione);
		
		JLabel lblStatoMonitor = new JLabel("Stato Monitor: Attivo");
		lblStatoMonitor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblStatoMonitor.setForeground(new Color(100, 116, 139));
		panelSimulazione.add(lblStatoMonitor);
		
		btnAvvioSimulazione = new JButton("Avvia simulazione");
		btnAvvioSimulazione.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnAvvioSimulazione.setForeground(colorTextDark);
		btnAvvioSimulazione.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panelSimulazione.add(btnAvvioSimulazione);
		
		btnInterrompi = new JButton("Interrompi");
		btnInterrompi.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnInterrompi.setForeground(new Color(225, 29, 72)); // Colore rosso scuro per stop
		btnInterrompi.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panelSimulazione.add(btnInterrompi);
		
		return panelSimulazione;
	}

	private JPanel buildPanelGestioneDati() {
		JPanel panelGestioneDati = new JPanel();
		panelGestioneDati.setBackground(colorCard);
		panelGestioneDati.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(colorBorder, 1, true),
			new EmptyBorder(6, 10, 6, 10)
		));
		panelGestioneDati.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 2));
		
		JButton saveButton = new JButton("Salva dati");
		saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		saveButton.setPreferredSize(new Dimension(100, 28));
		saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panelGestioneDati.add(saveButton);
		
		JButton loadButton = new JButton("Carica dati");
		loadButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		loadButton.setPreferredSize(new Dimension(100, 28));
		loadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelGestioneDati.add(loadButton);
		
		btnAggiungiStrumento = new JButton("Aggiungi Strumento");
		btnAggiungiStrumento.setPreferredSize(new Dimension(160, 28));
		btnAggiungiStrumento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnAggiungiStrumento.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panelGestioneDati.add(btnAggiungiStrumento);
		
		btnRimuoviStrumento = new JButton("Rimuovi Strumento");
		btnRimuoviStrumento.setPreferredSize(new Dimension(160, 28));
		btnRimuoviStrumento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnRimuoviStrumento.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelGestioneDati.add(btnRimuoviStrumento);
		
		return panelGestioneDati;
	}

	private JPanel buildUpperPanel() {
	    JPanel panelSuperiore = new JPanel();
	    panelSuperiore.setBackground(colorCard);
	    panelSuperiore.setBorder(BorderFactory.createCompoundBorder(
	        new LineBorder(colorBorder, 1, true),
	        new EmptyBorder(8, 10, 8, 10)
	    ));
	    panelSuperiore.setPreferredSize(new Dimension(0, 85));
	    panelSuperiore.setLayout(new GridLayout(1, 3, 10, 0));
	    
	    // --- 1. PANNELLO DISPONIBILI ---
	    JPanel panelDisponibili = new JPanel(new BorderLayout(0, 2));
	    panelDisponibili.setBackground(colorBg);
	    panelDisponibili.setBorder(BorderFactory.createCompoundBorder(
	        new LineBorder(colorBorder, 1, true),
	        new EmptyBorder(6, 10, 6, 10)
	    ));
	    
	    JLabel lblDisponibili = new JLabel("Disponibili");
	    lblDisponibili.setFont(new Font("Segoe UI", Font.BOLD, 12));
	    lblDisponibili.setForeground(new Color(100, 116, 139));
	    panelDisponibili.add(lblDisponibili, BorderLayout.NORTH);
	    
	    lblValoreDisponibili = new JLabel("0", SwingConstants.LEFT);
	    lblValoreDisponibili.setFont(new Font("Segoe UI", Font.BOLD, 22));
	    lblValoreDisponibili.setForeground(new Color(16, 185, 129)); // Verde
	    panelDisponibili.add(lblValoreDisponibili, BorderLayout.CENTER);
	    
	    panelSuperiore.add(panelDisponibili);

	    // --- 2. PANNELLO IN PRESTITO ---
	    JPanel panelPrestito = new JPanel(new BorderLayout(0, 2));
	    panelPrestito.setBackground(colorBg);
	    panelPrestito.setBorder(BorderFactory.createCompoundBorder(
	        new LineBorder(colorBorder, 1, true),
	        new EmptyBorder(6, 10, 6, 10)
	    ));
	    
	    JLabel lblPrestito = new JLabel("In prestito");
	    lblPrestito.setFont(new Font("Segoe UI", Font.BOLD, 12));
	    lblPrestito.setForeground(new Color(100, 116, 139));
	    panelPrestito.add(lblPrestito, BorderLayout.NORTH);
	    
	    lblValorePrestito = new JLabel("0", SwingConstants.LEFT);
	    lblValorePrestito.setFont(new Font("Segoe UI", Font.BOLD, 22));
	    lblValorePrestito.setForeground(new Color(59, 130, 246)); // Blu
	    panelPrestito.add(lblValorePrestito, BorderLayout.CENTER);
	    
	    panelSuperiore.add(panelPrestito);

	    // --- 3. PANNELLO IN ATTESA ---
	    JPanel panelAttesa = new JPanel(new BorderLayout(0, 2));
	    panelAttesa.setBackground(colorBg);
	    panelAttesa.setBorder(BorderFactory.createCompoundBorder(
	        new LineBorder(colorBorder, 1, true),
	        new EmptyBorder(6, 10, 6, 10)
	    ));
	    
	    JLabel lblAttesa = new JLabel("In attesa");
	    lblAttesa.setFont(new Font("Segoe UI", Font.BOLD, 12));
	    lblAttesa.setForeground(new Color(100, 116, 139));
	    panelAttesa.add(lblAttesa, BorderLayout.NORTH);
	    
	    lblValoreAttesa = new JLabel("0", SwingConstants.LEFT);
	    lblValoreAttesa.setFont(new Font("Segoe UI", Font.BOLD, 22));
	    lblValoreAttesa.setForeground(new Color(245, 158, 11)); // Arancione
	    panelAttesa.add(lblValoreAttesa, BorderLayout.CENTER);
	    
	    panelSuperiore.add(panelAttesa);

	    return panelSuperiore;
	}

	private JPanel buildMainPanel() {
		JPanel pannelloPrincipale = new JPanel();
		pannelloPrincipale.setBackground(colorBg);
		pannelloPrincipale.setBorder(new EmptyBorder(12, 12, 12, 12));
		pannelloPrincipale.setLayout(new BorderLayout(0, 10));
		return pannelloPrincipale;
	}
	
	/**
	 * Applica uno stile moderno e pulito alle tabelle JTable dell'applicazione.
	 */
	private void stilaTabella(JTable table) {
		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		table.setForeground(colorTextDark);
		table.setRowHeight(26);
		table.setGridColor(colorBorder);
		table.setFillsViewportHeight(true);
		table.setSelectionBackground(colorSelection);
		table.setSelectionForeground(colorTextDark);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 12));
		header.setBackground(colorHeaderBg);
		header.setForeground(colorTextDark);
		header.setReorderingAllowed(false);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, colorBorder));
	}
}