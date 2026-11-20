package it.unibs.eps.labmanager.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import it.unibs.eps.labmanager.model.Catalogo;
import it.unibs.eps.labmanager.model.EventLog;
import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.model.TipoStrumento;
import it.unibs.eps.labmanager.persistence.GestoreCatalogoFile;
import it.unibs.eps.labmanager.persistence.GestoreStoricoFile;

public class TestPersistence {

    public static void main(String[] args) throws IOException {
        List<String> errori = new ArrayList<>();

        try {
            // 1. Test salvataggio e caricamento Catalogo
            Catalogo catalogo = new Catalogo();
            catalogo.aggiungiStrumento(new Strumento("OSC-01", TipoStrumento.OSCILLOSCOPIO));
            catalogo.aggiungiStrumento(new Strumento("MUL-01", TipoStrumento.MULTIMETRO));

            GestoreCatalogoFile.salvaCatalogo(catalogo);
            Catalogo catalogoCaricato = GestoreCatalogoFile.caricaCatalogo();

            if (catalogoCaricato.getTuttiStrumenti().size() != 2) {
                errori.add("Numero strumenti caricati dal file non corretto.");
            }

            // 2. Test salvataggio e caricamento Storico
            List<EventLog> storico = List.of(
                new EventLog("Anna ottiene OSC-01"),
                new EventLog("Anna restituisce OSC-01")
            );

            GestoreStoricoFile.salvaStorico(storico);
            List<EventLog> storicoCaricato = GestoreStoricoFile.caricaStorico();

            if (storicoCaricato.size() != 2 || !storicoCaricato.get(0).getDescrizione().equals("Anna ottiene OSC-01")) {
                errori.add("Storico eventi non caricato correttamente.");
            }
        } finally {
            // Pulizia garantita dei file generati dal test
            Files.deleteIfExists(Path.of("catalogo_strumenti.txt"));
            Files.deleteIfExists(Path.of("storico_eventi.txt"));
        }

        // Riepilogo finale
        if (errori.isEmpty()) {
            System.out.println("TestPersistence: tutti i controlli superati!");
        } else {
            System.err.println("TestPersistence: rilevati " + errori.size() + " errori:");
            errori.forEach(err -> System.err.println("- " + err));
        }
    }
}
