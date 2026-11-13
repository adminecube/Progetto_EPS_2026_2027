package it.unibs.eps.labmanager.model;

public class InfoRichiesta {
	
	private final String nomeUtente;
	private StatoPrestito stato;
	private final TipoStrumento strumento;
	
	public InfoRichiesta(String nomeUtente, TipoStrumento tipoStrumento, StatoPrestito stato) {
        this.nomeUtente = nomeUtente;
        this.strumento = tipoStrumento;
        this.stato = stato;
    }

    public String getNomeUtente() { 
    	return nomeUtente; 
    	}
    public TipoStrumento getTipoStrumento() { 
    	return strumento; 
    	}
    public StatoPrestito getStato() { 
    	return stato; 
    	}
    public void setStato(StatoPrestito stato) { 
    	this.stato = stato; 
    	}

}
