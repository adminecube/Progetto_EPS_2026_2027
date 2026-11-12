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
 * Consente di salvare e ricaricare il registro degli EventLog separando l'orario
 * dalla descrizione dell'evento tramite il carattere separatore pipe '|'.
 * 
 * Le operazioni di I/O sono isolate in questa classe per evitare di eseguire accessi
 * ai file all'interno di blocchi sincronizzati o durante l'occupazione del monitor.
 */
public class GestoreStoricoFile {
	// Definisce il nome del file su cui verr� salvato lo storico
	private static final String FILE_STORICO = "storico_eventi.txt";
	
	private GestoreStoricoFile() {
		super();
	}
	/**
     * Salva l'intera lista di eventi presenti nello storico all'interno del file di testo.
     * Ciascun evento viene formattato come stringa nella forma "HH:mm:ss|Descrizione".
     *
     * @param storico La lista di oggetti {@link EventLog} da rendere persistenti su disco.
     */
	public static void salvaStorico(List<EventLog> storico) {
		// Apre il file in scrittura tramite BufferedWriter con try-with-resources per auto-chiusura
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_STORICO))){
    		for(EventLog log : storico) {
    			// Formatta l'evento separando l'orario dalla descrizione con il carattere pipe "|" (orario|descrizione)
    			// getOrarioFormattato() restituisce una String "HH:mm:ss"	
    			String linea = log.getOrarioFormattato() + "|" + log.getDescrizione();
    			writer.write(linea);
    			writer.newLine();
    		}
    	}catch(IOException e) {
    		System.err.println("Errore durante il salvataggio dello storico: " + e.getMessage());
    	}
    }
	
	/**
     * Carica dal file di testo lo storico degli eventi registrati nelle sessioni precedenti.
     * Ricostruisce ogni oggetto {@link EventLog} tramite il costruttore dedicato al ripristino dei dati.
     *
     * @return Una lista di oggetti {@link EventLog} popolata con i dati letti da file; 
     *         restituisce una lista vuota in caso di primo avvio o file non trovato.
     */
    public static List<EventLog> caricaStorico() {
    	List<EventLog> storicoCaricato = new ArrayList<>();
    	
    	// Usa un blocco try-with-resources per aprire in lettura il file in modo sicuro (BufferedReader)
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_STORICO))) {
        	// Dichiara una variabile per memorizzare temporaneamente ogni riga letta
        	String linea;
        	
        	// Continua a leggere riga per riga finch� non si raggiunge la fine del file (reader.readLine() restituisce null)
        	while ((linea = reader.readLine()) != null) {
        		// Divide la riga in un array di stringhe usando il punto e virgola come separatore
        		String[] parti = linea.split("\\|",2);
        		
        		// Verifica che la riga sia ben formattata e contenga esattamente 2 elementi (ID, Tipo, Stato)
        		if(parti.length==2) {
        			LocalTime orario = LocalTime.parse(parti[0]);
        			String descrizione = parti[1];
        			
        			// Usa il tuo Costruttore 2 per ricaricare gli eventi dal file
        			EventLog log = new EventLog(orario, descrizione);
        			storicoCaricato.add(log);
        		}
        	}
        	
        } catch (FileNotFoundException e) {
        	System.out.println("Nessun registro precedente trovato (primo avvio). Il registro partir� vuoto.");
		} catch (IOException e) {
			System.err.println("Errore di I/O durante la lettura dello storico: " + e.getMessage());
		} catch (DateTimeParseException e) {
			System.err.println("Errore nella formattazione dei dati nel file del registro.");
		}
        // Restituisce la lista (che sar� piena se il file c'era, o vuota se c'� stato un problema/primo avvio)
        return storicoCaricato;
    }
	
}

