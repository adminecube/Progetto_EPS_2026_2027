package it.unibs.eps.labmanager.model;

import java.util.Objects;

/**
 * @author biagi
 *
 */
public class Strumento {
	private String id;
	private TipoStrumento tipo;
	private StatoStrumento stato;
	/*
	 * Costruttore per creare un nuovo strumento
	 * Viene considerato Disponibile di default
	 * 
	 * @param id Identificativo univoco per lo strumento	(es:MUL-01)
	 * @param tipo Tipologia in cui cade lo strumento 		(es:Multimetro) 
	 */
	public Strumento (String id, TipoStrumento tipo) {
		//Controllo se i parametri non sono nulli o vuoti
		if(id==null||id.trim().isEmpty())
			throw new IllegalArgumentException("L'id dello strumento non può essere nullo o vuoto.");
		if(tipo==null)
			throw new IllegalArgumentException("Il tipo dello strumento non può essere nullo.");
		this.id=id;
		this.tipo=tipo;
		this.stato=StatoStrumento.DISPONIBILE;	//Stato iniziale di default
	}
	
	//Getters e setters
	public StatoStrumento getStato() {
		return stato;
	}
	public void setStato(StatoStrumento stato) {
		if(stato==null)
			throw new IllegalArgumentException("Lo stato non può essere nullo");
		else
			this.stato=stato;
	}
	public String getId() {
		return id;
	}
	public TipoStrumento getTipo() {
		return tipo;
	}
	//Ritorna true se Disponibile flase altrimenti
	public boolean isDisponibile() {
		return stato==StatoStrumento.DISPONIBILE;
	}
	
	// Sovrascriviamo equals() e hashCode() basandoci sull'ID univoco.
    // Questo è essenziale se metteremo gli strumenti in Set o Mappe (ad es. nel Catalogo).
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Strumento other = (Strumento) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Strumento [id=" + id + ", tipo=" + tipo + ", stato=" + stato + "]";
	}
	

	
}
