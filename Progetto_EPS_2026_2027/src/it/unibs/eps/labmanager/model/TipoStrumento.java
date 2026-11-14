package it.unibs.eps.labmanager.model;

/**
 * Elenca le categorie o le tipologie di strumenti gestiti e disponibili nel laboratorio.
 * @author Simone Biagi
 */
public enum TipoStrumento {
	
	OSCILLOSCOPIO("Oscilloscopio"),
	MULTIMETRO("Multimetro"),
	GENERATORE("Generatore");
	
	private final String nomeDescrittivo;
	
	/**
	 * Costruttore dell'enumerazione.
	 * 
	 * @param nomeDescrittivo Il nome testuale e formattato della tipologia di strumento
	 */
	TipoStrumento(String nomeDescrittivo) {
		this.nomeDescrittivo = nomeDescrittivo;
	}

	/**
	 * Restituisce il nome descrittivo del tipo di strumento.
	 * 
	 * @return Una stringa contenente il nome della tipologia
	 */
	public String getNomeDescrittivo() {
		return nomeDescrittivo;
	}
}