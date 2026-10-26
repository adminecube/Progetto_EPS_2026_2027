package it.unibs.eps.labmanager.model;

public enum StatoStrumento {
	//Lista degli stati in cui possono essere gli strumenti
	DISPONIBILE("Disponibile"),
	IN_PRESTITO("In Prestito");
	
	private final String nomeDescrittivo;
	
	StatoStrumento(String nomeDescrittivo) {
		this.nomeDescrittivo=nomeDescrittivo;
	}
	
	public String getNomeDescrittivo() {
		return nomeDescrittivo;
	}
	
}
