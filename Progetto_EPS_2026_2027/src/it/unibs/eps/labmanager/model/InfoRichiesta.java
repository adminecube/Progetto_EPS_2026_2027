package it.unibs.eps.labmanager.model;

public class InfoRichiesta {
	
	private String nomeUtente;
	private StatoPrestito stato;
	private TipoStrumento strumento;
	
	public InfoRichiesta() {
		
	}
	
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
