package it.unibs.eps.labmanager.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import it.unibs.eps.labmanager.model.Catalogo;
import it.unibs.eps.labmanager.model.StatoStrumento;
import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.model.TipoStrumento;

/**
 * Gestisce il salvataggio e il caricamento del catalogo degli strumenti su file testuale.
 */

public class GestoreCatalogoFile {
	// Definisce il nome del file su cui verr� salvato il catalogo
	private static final String FILE_CATALOGO = "catalogo_strumenti.txt";
    
	private GestoreCatalogoFile() {
		super();
	}
    /**
     * Salva la lista degli strumenti fornita su file.
     * Deve essere chiamato passando una copia dei dati e MAI mentre il monitor � bloccato.
     * 
     * @param strumenti
     */
	public static void salvaCatalogo(Catalogo catalogo) throws IOException {
        if (catalogo == null) {
            throw new IllegalArgumentException("Impossibile salvare un catalogo nullo.");
        }
        salvaCatalogo(catalogo.getTuttiStrumenti());
    }

    public static void salvaCatalogo(List<Strumento> listaStrumenti) throws IOException {
        if (listaStrumenti == null) {
            throw new IllegalArgumentException("Lista strumenti nulla.");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_CATALOGO))) {
            for (Strumento s : listaStrumenti) {
                String linea = s.getId() + ";" + s.getTipo() + ";" + s.getStato();
                writer.write(linea);
                writer.newLine();
            }
        }
    }

    /**
     * Carica gli strumenti dal file e restituisce una lista.
     * 
     * @return Una lista di Strumento caricata dal file, o una lista vuota se il file non esiste/� vuoto.
     */
    
    public static Catalogo caricaCatalogo() throws IOException {
        Catalogo catalogo = new Catalogo();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_CATALOGO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] parti = linea.split(";");
                if (parti.length == 3) {
                    String id = parti[0];
                    TipoStrumento tipo = TipoStrumento.valueOf(parti[1]);
                    
                    // Vedi nota sotto sullo stato al caricamento
                    Strumento strumento = new Strumento(id, tipo);
                    strumento.setStato(StatoStrumento.DISPONIBILE);
                    catalogo.aggiungiStrumento(strumento);
                }
            }
        } catch (FileNotFoundException e) {
            // Al primo avvio il file può non esistere: si restituisce un catalogo vuoto
            return catalogo;
        }
        return catalogo;
    }
    
}
