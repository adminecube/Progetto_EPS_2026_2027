package it.unibs.eps.labmanager.test;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("=== TestPersistence ===");
        TestPersistence.main(args);

        System.out.println("\n=== TestModel ===");
        TestModel.main(args);

        System.out.println("\n=== TestConcurrency ===");
        TestConcurrency.main(args);

        System.out.println("\n=== Tutti i test completati ===");
    }
}