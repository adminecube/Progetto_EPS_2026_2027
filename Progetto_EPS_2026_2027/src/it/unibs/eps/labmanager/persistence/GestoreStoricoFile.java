package it.unibs.eps.labmanager.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import it.unibs.eps.labmanager.model.EventLog;

/**
 * Gestisce la persistenza su file di testo dello storico degli eventi del laboratorio.
 * <p>
 * Consente di salvare e ricaricare il registro degli {@link EventLog} separando l'orario
 * dalla descrizione dell'evento tramite il carattere separatore pipe ('|').
 * </p>
 */
public class GestoreStoricoFile {
    
    /** Nome del file su cui verrà salvato lo storico */
    private static final String FILE_STORICO = "storico_eventi.txt";
    
    /**
     * Costruttore privato per impedire l'istanziazione di questa classe di utilità.
     */
    private GestoreStoricoFile() {
        super();
    }
    
    /**
     * Salva l'intera lista di eventi presenti nello storico all'interno del file di testo.
     *
     * @param storico La lista di oggetti {@link EventLog} da rendere persistenti su disco.
     * @throws IOException Se si verifica un problema di scrittura sul disco (es. disco pieno, permessi mancanti).
     */
    public static void salvaStorico(List<EventLog> storico) throws IOException {
        // Apre il file in scrittura tramite BufferedWriter con try-with-resources per auto-chiusura
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_STORICO))){
            for(EventLog log : storico) {
                // Formatta l'evento separando l'orario dalla descrizione con il carattere pipe "|"
                String linea = log.getOrarioFormattato() + "|" + log.getDescrizione();
                writer.write(linea);
                writer.newLine();
            }
        }
    }
    
    /**
     * Carica dal file di testo lo storico degli eventi registrati nelle sessioni precedenti.
     *
     * @return Una lista di oggetti {@link EventLog} popolata con i dati letti da file; 
     *         restituisce una lista vuota pacifica in caso di primo avvio (file non trovato).
     * @throws IOException Se si verifica un problema di lettura del file o se i dati 
     *                     temporali sono formattati in modo scorretto.
     */
    public static List<EventLog> caricaStorico() throws IOException {
        List<EventLog> storicoCaricato = new ArrayList<>();
        
        // Usa un blocco try-with-resources per aprire in lettura il file in modo sicuro
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_STORICO))) {
            String linea;
            
            // Continua a leggere riga per riga finché non si raggiunge la fine del file
            while ((linea = reader.readLine()) != null) {
                // Divide la riga in un array di stringhe usando il pipe come separatore
                String[] parti = linea.split("\\|", 2);
                
                if(parti.length == 2) {
                    LocalTime orario = LocalTime.parse(parti[0]);
                    String descrizione = parti[1];
                    
                    // Ricarica l'evento usando il costruttore per lo storico
                    EventLog log = new EventLog(orario, descrizione);
                    storicoCaricato.add(log);
                }
            }
            
        } catch (FileNotFoundException e) {
            // È normale che al primo avvio non ci sia un registro. 
            // Intercettiamo questa specifica eccezione e restituiamo la lista vuota senza errori.
            return storicoCaricato;
        } catch (DateTimeParseException e) {
            // Se il formato dell'ora è compromesso, impacchettiamo l'errore in una IOException
            throw new IOException("Errore nella formattazione dell'orario nel file del registro.", e);
        }
        
        return storicoCaricato;
    }
}