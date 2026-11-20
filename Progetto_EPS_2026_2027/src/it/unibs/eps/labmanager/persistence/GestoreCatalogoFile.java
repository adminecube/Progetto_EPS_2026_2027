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
 * Classe di utilità che gestisce il salvataggio e il caricamento del catalogo 
 * degli strumenti su file testuale.
 * <p>
 * Isola la logica di I/O dal resto dell'applicazione. 
 * </p>
 */
public class GestoreCatalogoFile {
    
    /** Nome del file su cui verrà salvato il catalogo */
    private static final String FILE_CATALOGO = "catalogo_strumenti.txt";
    
    /**
     * Costruttore privato per impedire l'istanziazione di questa classe di utilità.
     */
    private GestoreCatalogoFile() {
        super();
    }
    
    /**
     * Salva l'intero contenuto di un oggetto {@link Catalogo} su file.
     * <p>
     * Estrae la lista degli strumenti dal catalogo e la delega al metodo di salvataggio.
     * </p>
     * 
     * @param catalogo L'oggetto Catalogo da salvare. Deve contenere i dati aggiornati.
     * @throws IOException Se si verifica un errore durante l'apertura o la scrittura del file.
     * @throws IllegalArgumentException Se il catalogo passato è nullo.
     */
    public static void salvaCatalogo(Catalogo catalogo) throws IOException {
        if (catalogo == null) {
            throw new IllegalArgumentException("Impossibile salvare un catalogo nullo.");
        }
        salvaCatalogo(catalogo.getTuttiStrumenti());
    }

    /**
     * salva la lista degli strumenti su file testuale.
     * I campi di ogni strumento (ID, Tipo, Stato) vengono separati dal carattere punto e virgola (;).
     * 
     * @param listaStrumenti La lista di {@link Strumento} da rendere persistente.
     * @throws IOException Se si verifica un errore di scrittura sul disco.
     * @throws IllegalArgumentException Se la lista passata è nulla.
     */
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
     * Carica gli strumenti dal file di testo e ricostruisce un oggetto {@link Catalogo}.
     * <p>
     *  
     * @return Un nuovo {@link Catalogo} popolato con i dati del file, oppure 
     *         un catalogo vuoto se è il primo avvio e il file non esiste.
     * @throws IOException Se si verifica un errore di lettura o se il file è illeggibile.
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
                    
                    // Ripristino dello strumento (impostato sempre come DISPONIBILE all'avvio)
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