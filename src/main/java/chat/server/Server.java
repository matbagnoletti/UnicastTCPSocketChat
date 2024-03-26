package chat.server;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe che rappresenta il Server nella comunicazione secondo l'architettura C/S
 */
public class Server {
    
    /**
     * La connectionSocket del Server
     * @see ServerSocket
     */
    private ServerSocket serverSocket;
    
    /**
     * Il numero di porta su cui il Server si mette in ascolto
     */
    private final int porta;
    
    /**
     * Stream di input dal Client
     * @see BufferedReader
     */
    private BufferedReader input;

    /**
     * Scanner per l'input dell'utente
     * @see Scanner
     */
    private final Scanner scanner;

    /**
     * Variabile booleana che indica se il Server è attivo
     */
    private boolean attivo;

    /**
     * Codice ASCII colore rosso
     */
    private static final String ERRORE = "\033[31m";

    /**
     * Codice ASCII colore di reset
     */
    private static final String RESET = "\033[0m";

    /**
     * Costruttore
     * @param porta il numero di porta su cui il Server è in ascolto
     */
    public Server(int porta){
        this.porta = porta;
        this.scanner = new Scanner(System.in);
        this.attivo = true;
        try {
            serverSocket = new ServerSocket(this.porta);
            System.out.println("Server avviato e in ascolto sulla porta " + porta + "!");
            scrivi();
            
            while(attivo){
                Socket clientSocket = attendi();

                if(clientSocket != null){
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String username = input.readLine();
                    Chat chat = new Chat(clientSocket, username);
                    chat.start();
                }
            }

        } catch (BindException e) {
            System.err.println(ERRORE + "Errore: non e' possibile mettere il Server in ascolto sulla porta " + porta + RESET);
            chiudi();
        } catch (IOException e) {
            System.err.println(ERRORE + "Errore nell'apertura del Server: " + e.getMessage() + RESET);
            chiudi();
        }
    }

    /**
     * Metodo per l'accettazione della connessione con il Client e l'apertura degli stream
     * @return la socket con il Client
     */
    public Socket attendi(){
        try {
            return serverSocket.accept();
        } catch (IOException e){
            if(attivo){
                System.err.println(ERRORE + "Errore nell'attesa di connessioni: " + e.getMessage() + RESET);
            }
            return null;
        }
    }

    /**
     * Metodo per la lettura da tastiera dei messaggi e l'invio al Client
     */
    public void scrivi(){
        new Thread(() -> {
            System.out.println("Server pronto all'invio di messaggi! Digita '" + ERRORE +  "exit" + RESET + "' per terminare.");
            System.out.println("\n------------------------------------< Chat >------------------------------------\n");
            while(!serverSocket.isClosed()){
                try {
                    String[] messaggio = scanner.nextLine().split("->");
                    String payload = messaggio[0].trim();
                    if(payload.equals("exit")){
                        chiudi();
                    } else {
                        String username = messaggio[1].trim();
                        Chat.scrivi(username, payload);
                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    System.err.println(ERRORE + "Input invalido: atteso <payload> -> <username>" + RESET);
                }
            }
        }).start();
    }

    /**
     * Metodo di chiusura della socket e degli stream
     */
    public synchronized void chiudi(){
        attivo = false;
        try {
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
        } catch (IOException e){
            System.err.println(ERRORE + "Errore nella chiusura delle risorse: " + e.getMessage() + RESET);
        }
        System.exit(0);
    }
}
