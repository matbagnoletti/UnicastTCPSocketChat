package chat.client;

import java.util.Scanner;

public class MainClient {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Inserisci il tuo username: ");
        String nome = scanner.nextLine();

        String codColore = "\033[0m";
        do {
            System.out.print("Quale colore vuoi utilizzare per la chat? (R: rosso - G: giallo - V: verde - B: blu) ");
            String colore = scanner.nextLine();
            colore = colore.toUpperCase();
            switch (colore) {
                case "R" -> codColore = "\033[31m";
                case "G" -> codColore = "\033[33m";
                case "V" -> codColore = "\033[32m";
                case "B" -> codColore = "\033[34m";
                default -> System.out.println("La lettera inserita non è valida! Riprova.");
            }
        } while (codColore.equals("\033[0m"));
        Client client = new Client(nome, codColore);

        System.out.print("Inserici l'indirizzo IP del Server: ");
        String ip = scanner.nextLine();

        int porta = 19065;
        /* prevenire un'eccezione di tipo IllegalArgumentException nella creazione della socket */
        do {
            try {
                System.out.print("Inserisci un numero di porta valido (0 - 65535): ");
                porta = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Errore: hai inserito un valore non valido.");
            }
        } while (porta < 0 || porta > 65535);

        client.connetti(ip, porta);
    }
}
