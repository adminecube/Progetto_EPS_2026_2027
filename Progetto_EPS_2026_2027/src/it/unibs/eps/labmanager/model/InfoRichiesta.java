package it.unibs.eps.labmanager.model;

/**
 * Contiene i dettagli e le informazioni di base relative a una richiesta di prestito
 * effettuata da un utente nel sistema gestionale.
 * @author Lorenzo Manna
 */
public class InfoRichiesta {
	
	private final String nomeUtente;
	private StatoPrestito stato;
	private final TipoStrumento strumento;
	
	/**
	 * Costruttore per creare un record informativo riguardante una richiesta.
	 * 
	 * @param nomeUtente Il nome dell'utente che ha effettuato la richiesta
	 * @param tipoStrumento La tipologia dello strumento richiesto
	 * @param stato Lo stato attuale della richiesta (es. In Attesa, In Prestito, Rifiutato)
	 */
	public InfoRichiesta(String nomeUtente, TipoStrumento tipoStrumento, StatoPrestito stato) {
        this.nomeUtente = nomeUtente;
        this.strumento = tipoStrumento;
        this.stato = stato;
    }

	/**
	 * Restituisce il nome dell'utente che ha fatto la richiesta.
	 * 
	 * @return Il nome dell'utente
	 */
    public String getNomeUtente() { 
    	return nomeUtente; 
    }
    
    /**
     * Restituisce la tipologia dello strumento richiesto.
     * 
     * @return L'enumerazione del tipo strumento
     */
    public TipoStrumento getTipoStrumento() { 
    	return strumento; 
    }
    
    /**
     * Restituisce lo stato attuale della richiesta di prestito.
     * 
     * @return L'enumerazione indicante lo stato (es. In Attesa, Restituito, ecc.)
     */
    public StatoPrestito getStato() { 
    	return stato; 
    }
    
    /**
     * Modifica lo stato della richiesta di prestito (es. per approvarla, rifiutarla o concluderla).
     * 
     * @param stato Il nuovo stato da assegnare alla richiesta
     */
    public void setStato(StatoPrestito stato) { 
    	this.stato = stato; 
    }

}