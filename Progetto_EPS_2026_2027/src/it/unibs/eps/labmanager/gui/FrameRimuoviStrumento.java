package it.unibs.eps.labmanager.gui;

import java.awt.GridLayout;
import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import it.unibs.eps.labmanager.model.Strumento;

public class FrameRimuoviStrumento extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private List<Strumento> listaStrumenti;

    private JComboBox<Strumento> comboStrumenti;
    private JButton btnSalva;
    private JButton btnAnnulla;
    

    public FrameRimuoviStrumento(Consumer<Strumento> rimuoviStrumento) {
    	
    	listaStrumenti = new ArrayList<Strumento>();
        setTitle("Aggiungi Strumento");
        setSize(600, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Strumento:"));
        comboStrumenti = new JComboBox<Strumento>(listaStrumenti.toArray(new Strumento[0]));
        panel.add(comboStrumenti);

        btnSalva = new JButton("Rimuovi");
        btnAnnulla = new JButton("Annulla");
        panel.add(btnSalva);
        panel.add(btnAnnulla);

        add(panel);

        // Azione del tasto Salva
        btnSalva.addActionListener(e -> {
        	
        	 try {
        		 Strumento strumento = (Strumento) comboStrumenti.getSelectedItem();
             	 rimuoviStrumento.accept(strumento);
                 setVisible(false);
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
             }
           
        });

        btnAnnulla.addActionListener(e -> setVisible(false));
    }
    
    public void aggiornaListaStrumenti(List<Strumento> listaStrumenti) {
    	
    	this.listaStrumenti = listaStrumenti;
        comboStrumenti.removeAllItems();
        for (Strumento s : listaStrumenti) {
            comboStrumenti.addItem(s);
        }
    }

}
