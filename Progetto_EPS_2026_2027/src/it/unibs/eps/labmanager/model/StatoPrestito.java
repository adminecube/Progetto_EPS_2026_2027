package it.unibs.eps.labmanager.model;

public enum StatoPrestito {
	RESTITUITO("Restituito"),
	IN_ATTESA("In Attesa"),
	IN_PRESTITO("In Prestito"),
	RIFIUTATO("Rifiutato");
	
	private String nomeDescrittivo;
	StatoPrestito(String nomeDescrittivo) {
		this.nomeDescrittivo=nomeDescrittivo;
	}
	public String getNomeDescrittivo() {
		return nomeDescrittivo;
	}
	
}
