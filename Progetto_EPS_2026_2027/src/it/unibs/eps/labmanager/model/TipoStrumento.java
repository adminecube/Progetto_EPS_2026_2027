package it.unibs.eps.labmanager.model;

public enum TipoStrumento {
	//Lista delle categorie degli strumenti
	OSCILLOSCOPIO("Oscilloscopio"),
	MULTIMETRO("Multimetro"),
	GENERATORE("Generatore");
	
	private final String nomeDescrittivo;
	
	TipoStrumento(String nomeDescrittivo) {
		this.nomeDescrittivo=nomeDescrittivo;
	}

	public String getNomeDescrittivo() {
		return nomeDescrittivo;
	}
}
