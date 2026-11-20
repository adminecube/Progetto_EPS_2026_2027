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

/**
 * Finestra di dialogo dedicata alla rimozione di uno {@link Strumento} esistente 
 * dal catalogo del laboratorio.
 * <p>
 * Mostra un menu a tendina contenente l'elenco aggiornato degli strumenti disponibili
 * e delega l'effettiva logica di eliminazione a una callback passata dal frame principale.
 * </p>
 */
public class FrameRimuoviStrumento extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private List<Strumento> listaStrumenti;

    private JComboBox<Strumento> comboStrumenti;
    private JButton btnSalva;
    private JButton btnAnnulla;
    
    /**
     * Costruisce la finestra per la rimozione di uno strumento.
     *
     * @param rimuoviStrumento Callback di tipo {@link Consumer} che viene invocata 
     *                         quando l'utente conferma la rimozione. Riceve come parametro 
     *                         lo {@link Strumento} selezionato da rimuovere.
     */
    public FrameRimuoviStrumento(Consumer<Strumento> rimuoviStrumento) {
    	
    	listaStrumenti = new ArrayList<Strumento>();
        setTitle("Rimuovi Strumento"); // Modificato il titolo da "Aggiungi Strumento" a "Rimuovi Strumento"
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

        // Azione del tasto Rimuovi
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
    
    /**
     * Aggiorna il contenuto del menu a tendina (JComboBox) con la lista più recente 
     * degli strumenti presenti nel sistema.
     * <p>
     * Questo metodo deve essere chiamato prima di rendere visibile il frame per 
     * garantire che i dati mostrati siano sincronizzati con lo stato del Monitor.
     * </p>
     *
     * @param listaStrumenti La lista aggiornata di strumenti da mostrare.
     */
    public void aggiornaListaStrumenti(List<Strumento> listaStrumenti) {
    	this.listaStrumenti = listaStrumenti;
        comboStrumenti.removeAllItems();
        for (Strumento s : listaStrumenti) {
            comboStrumenti.addItem(s);
        }
    }
}