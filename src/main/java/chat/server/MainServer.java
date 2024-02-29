package chat.server;

import java.util.Scanner;

public class MainServer {
    public synchronized static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        int porta = 19065;
        /* prevenire un'eccezione di tipo IllegalArgumentException nella creazione della socket */
        do {
            try {
                System.out.print("Inserisci un numero di porta valido in cui avviare il Server (0 - 65535): ");
                porta = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Errore: hai inserito un valore non valido.");
            }
        } while (porta < 0 || porta > 65535);

        Server server = new Server(porta);
    }
}
