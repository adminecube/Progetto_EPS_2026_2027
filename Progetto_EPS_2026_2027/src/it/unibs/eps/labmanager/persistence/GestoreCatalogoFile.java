package it.unibs.eps.labmanager.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import it.unibs.eps.labmanager.model.Catalogo;
import it.unibs.eps.labmanager.model.StatoStrumento;
import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.model.TipoStrumento;

/**
 * Gestisce il salvataggio e il caricamento del catalogo degli strumenti su file testuale.
 */

public class GestoreCatalogoFile {
	// Definisce il nome del file su cui verrà salvato il catalogo
	private static final String FILE_CATALOGO = "catalogo_strumenti.txt";
    
    /**
     * Salva la lista degli strumenti fornita su file.
     * Deve essere chiamato passando una copia dei dati e MAI mentre il monitor è bloccato.
     * 
     * @param strumenti
     */
    public void salvaCatalogo(Catalogo catalogo) {
    	if (catalogo == null) {
            System.err.println("Impossibile salvare un catalogo nullo.");
            return;
        }
    	// Usa un blocco try-with-resources per garantire la chiusura automatica del file (BufferedWriter)
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_CATALOGO))){
    		for(Strumento s:catalogo.getTuttiStrumenti()) {
    			// Formatta i dati dello strumento separandoli con un punto e virgola (ID;TIPO;STATO)
    			String linea = s.getId()+";"+s.getTipo()+";"+s.getStato();
    			writer.write(linea);
    			writer.newLine();
    		}
    	}catch(IOException e) {
    		System.err.println("Errore durante il salvataggio del catalogo: " + e.getMessage());
    	}
    }
    
    /**
     * Carica gli strumenti dal file e restituisce una lista.
     * 
     * @return Una lista di Strumento caricata dal file, o una lista vuota se il file non esiste/è vuoto.
     */
    public Catalogo caricaCatalogo() {
    	Catalogo catalogo = new Catalogo();
    	
    	// Usa un blocco try-with-resources per aprire in lettura il file in modo sicuro (BufferedReader)
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_CATALOGO))) {
        	// Dichiara una variabile per memorizzare temporaneamente ogni riga letta
        	String linea;
        	
        	// Continua a leggere riga per riga finché non si raggiunge la fine del file (reader.readLine() restituisce null)
        	while ((linea = reader.readLine()) != null) {
        		// Divide la riga in un array di stringhe usando il punto e virgola come separatore
        		String[] parti = linea.split(";");
        		
        		// Verifica che la riga sia ben formattata e contenga esattamente 3 elementi (ID, Tipo, Stato)
        		if(parti.length==3) {
        			String id = parti[0];
        			TipoStrumento tipo = TipoStrumento.valueOf(parti[1]);
        			StatoStrumento stato = StatoStrumento.valueOf(parti[2]);
        			
        			//Creo un nuovo oggetto strumento con le caratteristiche rilevate
        			Strumento strumento = new Strumento(id,tipo);
        			strumento.setStato(stato);
        			//lo aggiungo alla lista strumenti caricati
        			catalogo.aggiungiStrumento(strumento);
        		}
        	}
        	
        } catch (FileNotFoundException e) {
        	System.out.println("Nessun catalogo precedente trovato (primo avvio). Il catalogo partirà vuoto.");
		} catch (IOException e) {
			System.err.println("Errore di I/O durante la lettura del catalogo: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println("Errore nella formattazione dei dati nel file del catalogo.");
		}
        // Restituisce la lista (che sarà piena se il file c'era, o vuota se c'è stato un problema/primo avvio)
        return catalogo;
    }
    
}
