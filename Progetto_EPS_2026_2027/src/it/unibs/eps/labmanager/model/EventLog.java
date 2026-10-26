package it.unibs.eps.labmanager.model;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

//Storico eventi del laboratorio
public class EventLog {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	private final LocalTime orario;
	private final String descrizione;
	
	//Costruttore 1: per eventi in tempo reale
	public EventLog(String descrizione) {
		this.descrizione=descrizione;
		orario=LocalTime.now();
	}
	//Costruttore 2: Per il caricamento da file o da test (es per il caricamento di un file con lo stroico eventi)
	public EventLog(LocalTime orario,String descrizione) {
		this.orario=Objects.requireNonNull(orario, "Orario non valido.");
		this.descrizione=Objects.requireNonNull(descrizione,"Descrizione non valida");
	}
	
	//Getters
	public String getOrarioFormattato() {
		return orario.format(FORMATTER);
	}
	public LocalTime getOrario() {
		return orario;
	}
	public String getDescrizione() {
		return descrizione;
	}
	@Override
	public String toString() {
		return String.format("%s | %s", getOrarioFormattato(), descrizione);
	}
	
	
}

