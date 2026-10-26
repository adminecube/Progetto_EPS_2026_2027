package it.unibs.eps.labmanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *	Contenitore e gestore dell'insieme di strumenti fisici presenti nel laboratorio.
 * 	@author biagi
 *
 */
public class Catalogo {
	private final List<Strumento> strumenti;
	
	public Catalogo() {
		this.strumenti=new ArrayList<>();
	}
	//metodi 
	public void aggiungiStrumento(Strumento strumento){
		if(strumenti.contains(strumento))
			throw new IllegalArgumentException("Lo strumento con codice " + strumento.getId() + "e' gia presente");
		else if(strumento == null)
			throw new IllegalArgumentException("Lo strumento non puo' essere nullo");
		else
			strumenti.add(strumento);
	}
	
	
	/**
	 * @param tipo 	il tipo di strumento richiesto
	 * @return		ritorna il primo strumento libero richiesto, oppure ritorna null se non è disponibile nessuno strumento
	 */
	public Strumento trovaStrumentoDisponibile(TipoStrumento tipo) {
		return strumenti.stream()					// 1. Converte la lista degli strumenti in un flusso di dati (Stream)
				.filter(s-> s.getTipo() == tipo)	// 2. Mantiene nello stream solo gli strumenti della categoria richiesta
				.filter(Strumento::isDisponibile)	// 3. Mantiene solo quelli che risultano attualmente liberi (stato DISPONIBILE)
				.findFirst()						// 4. Interrompe la ricerca al primo elemento valido e lo racchiude in un Optional<Strumento>
				.orElse(null);						// 5. Estrae lo strumento dall'Optional; se vuoto, restituisce null
	}
	
	public List<Strumento> getTuttiStrumenti(){
		return Collections.unmodifiableList(strumenti);
	}
	
	public List<Strumento> getStrumentiDisponibili(){
		return strumenti.stream()
				.filter(s->s.getStato() == StatoStrumento.DISPONIBILE)
				.collect(Collectors.toList());
	}
	
	public List<Strumento> getStrumentiInPrestito(){
		return strumenti.stream()
				.filter(s->s.getStato() == StatoStrumento.IN_PRESTITO)
				.collect(Collectors.toList());
	}
	@Override
	public String toString() {
	    if (strumenti.isEmpty()) {
	        return "Catalogo vuoto.";
	    }

	    StringBuilder sb = new StringBuilder("=== CATALOGO STRUMENTI ===\n");
	    for (Strumento s : strumenti) {
	        sb.append("- ").append(s.toString()).append("\n");
	    }
	    return sb.toString().trim();
	}
	
	
}
