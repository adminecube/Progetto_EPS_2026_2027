package it.unibs.eps.labmanager.test;

import java.util.ArrayList;
import java.util.List;

import it.unibs.eps.labmanager.model.Catalogo;
import it.unibs.eps.labmanager.model.EventLog;
import it.unibs.eps.labmanager.model.InfoRichiesta;
import it.unibs.eps.labmanager.model.StatoPrestito;
import it.unibs.eps.labmanager.model.StatoStrumento;
import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.model.TipoStrumento;

public class TestModel {

    public static void main(String[] args) {
        List<String> errori = new ArrayList<>();

        // 1. Test Strumento e cambio stato
        Strumento s1 = new Strumento("OSC-01", TipoStrumento.OSCILLOSCOPIO);
        if (!s1.isDisponibile() || s1.getStato() != StatoStrumento.DISPONIBILE) {
            errori.add("Uno strumento nuovo deve essere DISPONIBILE.");
        }

        s1.setStato(StatoStrumento.IN_PRESTITO);
        if (s1.isDisponibile()) {
            errori.add("Lo strumento dovrebbe risultare IN_PRESTITO.");
        }

        // 2. Test Catalogo (ricerca, vincolo max 5 strumenti e rimozione)
        Catalogo catalogo = new Catalogo();
        catalogo.aggiungiStrumento(s1);

        if (catalogo.trovaStrumentoDisponibile(TipoStrumento.OSCILLOSCOPIO) != null) {
            errori.add("Non dovrebbero esserci oscilloscopi disponibili.");
        }

        s1.setStato(StatoStrumento.DISPONIBILE);
        if (catalogo.trovaStrumentoDisponibile(TipoStrumento.OSCILLOSCOPIO) != s1) {
            errori.add("s1 dovrebbe essere trovato come disponibile.");
        }

        catalogo.aggiungiStrumento(new Strumento("MUL-01", TipoStrumento.MULTIMETRO));
        catalogo.aggiungiStrumento(new Strumento("MUL-02", TipoStrumento.MULTIMETRO));
        catalogo.aggiungiStrumento(new Strumento("GEN-01", TipoStrumento.GENERATORE));
        catalogo.aggiungiStrumento(new Strumento("GEN-02", TipoStrumento.GENERATORE));

        try {
            catalogo.aggiungiStrumento(new Strumento("OSC-02", TipoStrumento.OSCILLOSCOPIO));
            errori.add("Il catalogo non dovrebbe accettare più di 5 strumenti.");
        } catch (IllegalArgumentException e) {
            // Comportamento atteso
        }

        catalogo.rimuoviStrumento(s1);
        if (catalogo.getTuttiStrumenti().size() != 4) {
            errori.add("Rimozione strumento dal catalogo fallita.");
        }

        // 3. Test InfoRichiesta ed EventLog
        InfoRichiesta richiesta = new InfoRichiesta("Anna", TipoStrumento.MULTIMETRO, StatoPrestito.IN_ATTESA);
        richiesta.setStato(StatoPrestito.IN_PRESTITO);
        if (richiesta.getStato() != StatoPrestito.IN_PRESTITO) {
            errori.add("Aggiornamento stato richiesta fallito.");
        }

        EventLog evento = new EventLog("Anna ottiene MUL-01");
        if (!"Anna ottiene MUL-01".equals(evento.getDescrizione()) || evento.getOrarioFormattato() == null) {
            errori.add("Creazione EventLog non valida.");
        }

        // Riepilogo finale
        if (errori.isEmpty()) {
            System.out.println("TestModel: tutti i controlli superati!");
        } else {
            System.err.println("TestModel: rilevati " + errori.size() + " errori:");
            errori.forEach(err -> System.err.println("- " + err));
        }
    }
}
