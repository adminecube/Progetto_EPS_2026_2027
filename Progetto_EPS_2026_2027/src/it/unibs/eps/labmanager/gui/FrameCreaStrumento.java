package it.unibs.eps.labmanager.gui;

import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.model.TipoStrumento;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class FrameCreaStrumento extends JFrame {

    private JTextField txtId;
    private JComboBox<TipoStrumento> comboTipo;
    private JButton btnSalva;
    private JButton btnAnnulla;

    public FrameCreaStrumento(Consumer<Strumento> onStrumentoCreato) {
        setTitle("Aggiungi Strumento");
        setSize(320, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Tipo Strumento:"));
        comboTipo = new JComboBox<>(TipoStrumento.values());
        panel.add(comboTipo);

        panel.add(new JLabel("Codice (ID):"));
        txtId = new JTextField();
        panel.add(txtId);

        btnSalva = new JButton("Salva");
        btnAnnulla = new JButton("Annulla");
        panel.add(btnSalva);
        panel.add(btnAnnulla);

        add(panel);

        // Azione del tasto Salva
        btnSalva.addActionListener(e -> {
            String id = txtId.getText().trim();
            TipoStrumento tipo = (TipoStrumento) comboTipo.getSelectedItem();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inserisci un ID valido!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Strumento nuovoStrumento = new Strumento(id, tipo);
                onStrumentoCreato.accept(nuovoStrumento); // Invia lo strumento a MainFrame
                txtId.setText("");
                setVisible(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAnnulla.addActionListener(e -> setVisible(false));
    }
}