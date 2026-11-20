package it.unibs.eps.labmanager.test;

import java.util.ArrayList;
import java.util.List;

import it.unibs.eps.labmanager.concurrency.MonitorLaboratorio;
import it.unibs.eps.labmanager.model.Catalogo;
import it.unibs.eps.labmanager.model.InfoRichiesta;
import it.unibs.eps.labmanager.model.StatoPrestito;
import it.unibs.eps.labmanager.model.StatoStrumento;
import it.unibs.eps.labmanager.model.Strumento;
import it.unibs.eps.labmanager.model.TipoStrumento;

public class TestConcurrency {

    public static void main(String[] args) throws InterruptedException {
        List<String> errori = new ArrayList<>();

        //Test assegnazione immediata e invocazione diretta del Listener
        Catalogo catalogo = new Catalogo();
        Strumento osc1 = new Strumento("OSC-01", TipoStrumento.OSCILLOSCOPIO);
        catalogo.aggiungiStrumento(osc1);

        MonitorLaboratorio monitor = new MonitorLaboratorio(catalogo);
        final int[] notifiche = {0};
        monitor.setListener(() -> {
            synchronized (notifiche) {
                notifiche[0]++;
            }
        });

        Strumento assegnato = monitor.richiediStrumento(TipoStrumento.OSCILLOSCOPIO, "Anna");
        if (assegnato != osc1 || osc1.getStato() != StatoStrumento.IN_PRESTITO) {
            errori.add("Assegnazione immediata dello strumento disponibile fallita.");
        }
        if (notifiche[0] != 1) {
            errori.add("Il listener doveva essere invocato direttamente 1 volta subito dopo la richiesta.");
        }

        // Verifica vincolo: impossibile rimuovere uno strumento in prestito
        try {
            monitor.rimuoviStrumento(osc1);
            errori.add("Non deve essere possibile rimuovere uno strumento IN_PRESTITO.");
        } catch (IllegalStateException e) {
            // Comportamento atteso
        }

        //Test attesa (wait) su risorsa occupata e risveglio su restituzione (notifyAll)
        final Object lockAvvioLuca = new Object();
        final boolean[] lucaAvviato = {false};
        final Strumento[] risultatoLuca = new Strumento[1];

        Thread threadLuca = new Thread(() -> {
            synchronized (lockAvvioLuca) {
                lucaAvviato[0] = true;
                lockAvvioLuca.notify();
            }
            risultatoLuca[0] = monitor.richiediStrumento(TipoStrumento.OSCILLOSCOPIO, "Luca");
        });

        synchronized (lockAvvioLuca) {
            threadLuca.start();
            while (!lucaAvviato[0]) {
                lockAvvioLuca.wait();
            }
        }

        // Anna restituisce lo strumento -> Luca si risveglia e lo acquisisce
        monitor.restituisciStrumento(osc1, "Anna");
        threadLuca.join();

        if (risultatoLuca[0] != osc1 || osc1.getStato() != StatoStrumento.IN_PRESTITO) {
            errori.add("Luca non ha acquisito correttamente lo strumento dopo la restituzione.");
        }

        //Test gestione dell'interruzione (InterruptedException) durante l'attesa
        final Object lockAvvioSara = new Object();
        final boolean[] saraAvviata = {false};
        final Strumento[] risultatoSara = {osc1};

        Thread threadSara = new Thread(() -> {
            synchronized (lockAvvioSara) {
                saraAvviata[0] = true;
                lockAvvioSara.notify();
            }
            risultatoSara[0] = monitor.richiediStrumento(TipoStrumento.OSCILLOSCOPIO, "Sara");
        });

        synchronized (lockAvvioSara) {
            threadSara.start();
            while (!saraAvviata[0]) {
                lockAvvioSara.wait();
            }
        }

        threadSara.interrupt();
        threadSara.join();

        if (risultatoSara[0] != null) {
            errori.add("Il thread interrotto doveva terminare restituendo null.");
        }

        boolean saraRifiutata = false;
        for (InfoRichiesta r : monitor.getListaRichieste()) {
            if (r.getNomeUtente().equals("Sara") && r.getStato() == StatoPrestito.RIFIUTATO) {
                saraRifiutata = true;
                break;
            }
        }
        if (!saraRifiutata || osc1.getStato() != StatoStrumento.IN_PRESTITO) {
            errori.add("L'interruzione ha lasciato lo stato del laboratorio incoerente.");
        }

        //Restituzione finale da parte di Luca e verifica contatori
        monitor.restituisciStrumento(osc1, "Luca");
        if (monitor.getNumeroDisponibili() != 1 || monitor.getNumeroInPrestito() != 0) {
            errori.add("Conteggi finali disponibili/in prestito non coerenti.");
        }

        // Riepilogo finale
        if (errori.isEmpty()) {
            System.out.println("TestConcurrency: tutti i controlli superati!");
        } else {
            System.err.println("TestConcurrency: rilevati " + errori.size() + " errori:");
            for (String err : errori) {
                System.err.println("- " + err);
            }
        }
    }
}
