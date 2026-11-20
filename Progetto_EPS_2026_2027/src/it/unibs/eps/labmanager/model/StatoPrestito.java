package it.unibs.eps.labmanager.model;

/**
 * Rappresenta lo stato corrente di un prestito all'interno del sistema gestionale.
 * @author Simone Biagi
 */
public enum StatoPrestito {

	RESTITUITO("Restituito"),
	IN_ATTESA("In Attesa"),
	IN_PRESTITO("In Prestito"),
	RIFIUTATO("Rifiutato");
	
	private String nomeDescrittivo;
	
	/**
	 * Costruttore dell'enumerazione.
	 * 
	 * @param nomeDescrittivo Il nome in formato leggibile associato allo stato del prestito
	 */
	StatoPrestito(String nomeDescrittivo) {
		this.nomeDescrittivo = nomeDescrittivo;
	}
	
	/**
	 * Restituisce il nome descrittivo dello stato del prestito (es. per interfacce grafiche o stampe).
	 * 
	 * @return Una stringa contenente il nome descrittivo
	 */
	public String getNomeDescrittivo() {
		return nomeDescrittivo;
	}
	
}