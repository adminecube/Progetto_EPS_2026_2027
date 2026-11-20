package it.unibs.eps.labmanager.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Rappresenta una singola voce nello storico degli eventi del laboratorio.
 * Ogni EventLog memorizza una descrizione testuale dell'evento e l'orario 
 * esatto in cui è avvenuto.
 * @author Simone Biagi
 */
public class EventLog {
	
	/** Formattatore standard per visualizzare l'orario nel formato "Ore:Minuti:Secondi" */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	private final LocalTime orario;
	private final String descrizione;
	
	/**
	 * Costruttore per registrare eventi in tempo reale.
	 * L'orario dell'evento viene impostato automaticamente al momento della creazione dell'oggetto.
	 * 
	 * @param descrizione Testo che descrive l'evento
	 */
	public EventLog(String descrizione) {
		this.descrizione = descrizione;
		this.orario = LocalTime.now();
	}
	
	/**
	 * Costruttore per eventi con orario predefinito.
	 *  
	 * @param orario L'orario esatto in cui si è verificato l'evento
	 * @param descrizione Testo che descrive l'evento
	 * @throws NullPointerException Se l'orario o la descrizione passati sono nulli
	 */
	public EventLog(LocalTime orario, String descrizione) {
		this.orario = Objects.requireNonNull(orario, "Orario non valido.");
		this.descrizione = Objects.requireNonNull(descrizione, "Descrizione non valida");
	}
	
	/**
	 * Restituisce l'orario dell'evento formattato come stringa.
	 * 
	 * @return La stringa dell'orario in formato "HH:mm:ss"
	 */
	public String getOrarioFormattato() {
		return orario.format(FORMATTER);
	}
	
	/**
	 * Restituisce l'oggetto LocalTime corrispondente all'orario dell'evento.
	 * 
	 * @return L'orario in cui si è verificato l'evento
	 */
	public LocalTime getOrario() {
		return orario;
	}
	
	/**
	 * Restituisce la descrizione testuale dell'evento.
	 * 
	 * @return Il testo dell'evento
	 */
	public String getDescrizione() {
		return descrizione;
	}
	
	/**
	 * Fornisce una rappresentazione testuale formattata dell'EventLog, utile per la stampa a video o su file.
	 * 
	 * @return Una stringa strutturata come "[Orario Formattato] | [Descrizione]"
	 */
	@Override
	public String toString() {
		return String.format("%s | %s", getOrarioFormattato(), descrizione);
	}
	
}