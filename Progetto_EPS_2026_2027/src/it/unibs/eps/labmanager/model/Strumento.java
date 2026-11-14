package it.unibs.eps.labmanager.model;

import java.util.Objects;

/**
 * Rappresenta un singolo strumento del laboratorio.
 * Ogni strumento è identificato in modo univoco da un ID, possiede 
 * una tipologia e uno stato corrente di disponibilità.
 * 
 * @author Simone Biagi
 */
public class Strumento {
	private String id;
	private TipoStrumento tipo;
	private StatoStrumento stato;
	
	/**
	 * Costruttore per creare un nuovo strumento.
	 * Al momento della creazione, lo strumento viene considerato {@link StatoStrumento#DISPONIBILE} di default.
	 * 
	 * @param id Identificativo univoco per lo strumento (es. "MUL-01")
	 * @param tipo Tipologia in cui cade lo strumento (es. Multimetro, Oscilloscopio)
	 * @throws IllegalArgumentException Se l'ID è nullo, vuoto o se il tipo è nullo
	 */
	public Strumento (String id, TipoStrumento tipo) {
		//Controllo se i parametri non sono nulli o vuoti
		if(id == null || id.trim().isEmpty())
			throw new IllegalArgumentException("L'id dello strumento non può essere nullo o vuoto.");
		if(tipo == null)
			throw new IllegalArgumentException("Il tipo dello strumento non può essere nullo.");
		
		this.id = id;
		this.tipo = tipo;
		this.stato = StatoStrumento.DISPONIBILE; //Stato iniziale di default
	}
	
	/**
	 * Restituisce lo stato attuale dello strumento.
	 * 
	 * @return Lo stato dello strumento (Disponibile o In Prestito)
	 */
	public StatoStrumento getStato() {
		return stato;
	}
	
	/**
	 * Modifica lo stato operativo dello strumento.
	 * 
	 * @param stato Il nuovo stato da assegnare allo strumento
	 * @throws IllegalArgumentException Se lo stato passato come parametro è nullo
	 */
	public void setStato(StatoStrumento stato) {
		if(stato == null)
			throw new IllegalArgumentException("Lo stato non può essere nullo");
		else
			this.stato = stato;
	}
	
	/**
	 * Restituisce l'identificativo univoco dello strumento.
	 * 
	 * @return L'ID dello strumento
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Restituisce la tipologia a cui appartiene lo strumento.
	 * 
	 * @return Il tipo dello strumento
	 */
	public TipoStrumento getTipo() {
		return tipo;
	}
	
	/**
	 * Verifica se lo strumento è attualmente disponibile per un nuovo prestito.
	 * 
	 * @return {@code true} se lo strumento è disponibile, {@code false} altrimenti
	 */
	public boolean isDisponibile() {
		return stato == StatoStrumento.DISPONIBILE;
	}
	
	/**
	 * Calcola l'hash code dello strumento basandosi esclusivamente sul suo ID univoco.
	 * 
	 * @return Il valore hash dell'oggetto
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Verifica l'uguaglianza tra questo strumento e un altro oggetto.
	 * Due strumenti sono considerati uguali se e solo se possiedono lo stesso ID univoco.
	 * 
	 * @param obj L'oggetto con cui confrontare lo strumento
	 * @return {@code true} se gli oggetti hanno lo stesso ID, {@code false} altrimenti
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Strumento other = (Strumento) obj;
		return Objects.equals(id, other.id);
	}

	/**
	 * Restituisce una rappresentazione testuale dello strumento, 
	 * includendo il suo ID, il tipo e lo stato attuale.
	 * 
	 * @return Una stringa che descrive lo strumento
	 */
	@Override
	public String toString() {
		return "Strumento [id=" + id + ", tipo=" + tipo + ", stato=" + stato + "]";
	}
	
}