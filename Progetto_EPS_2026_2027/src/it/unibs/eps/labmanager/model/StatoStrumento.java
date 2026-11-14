package it.unibs.eps.labmanager.model;

/**
 * Definisce i possibili stati operativi in cui può trovarsi uno strumento del laboratorio.
 * @author Simone Biagi
 */
public enum StatoStrumento {

	DISPONIBILE("Disponibile"),
	IN_PRESTITO("In Prestito");
	
	private final String nomeDescrittivo;
	
	/**
	 * Costruttore dell'enumerazione.
	 * 
	 * @param nomeDescrittivo Il nome in formato leggibile associato allo stato dello strumento
	 */
	StatoStrumento(String nomeDescrittivo) {
		this.nomeDescrittivo = nomeDescrittivo;
	}
	
	/**
	 * Restituisce il nome descrittivo dello stato dello strumento.
	 * 
	 * @return Una stringa contenente il nome descrittivo
	 */
	public String getNomeDescrittivo() {
		return nomeDescrittivo;
	}
	
}