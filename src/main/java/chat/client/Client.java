package chat.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private final String nome;
    private final String colore;
    private static final String ERRORE = "\033[31m";
    private static final String RESET = "\033[0m";
    private Socket socket;
    /**
     * Stream di output verso il Server
     */
    private BufferedWriter output;
    /**
     * Stream di input dal Server
     */
    private BufferedReader input;
    private final Scanner scanner;
    public Client(String nomeDefault, String coloreDefault){
        this.nome = nomeDefault;
        this.colore = coloreDefault;
        scanner = new Scanner(System.in);
    }
    public void connetti(String nomeServer, int portaServer){
        try {
            socket = new Socket(nomeServer, portaServer);
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output.write(nome);
            output.newLine();
            output.flush();

            leggi();
            scrivi();
        } catch (UnknownHostException e){
            System.err.println(ERRORE + "Errore nella connessione al Server: controlla di aver inserito un indirizzo IP valido" + RESET);
            chiudi();
        } catch (IOException e){
            System.err.println(ERRORE + "Errore nella creazione della socket con il Server: " + e.getMessage() + RESET);
            chiudi();
        }
    }

    /**
     * Metodo per la lettura da tastiera dei messaggi e l'invio al Server
     */
    public void scrivi(){
        System.out.println("Client pronto all'invio di messaggi! Digita '" + ERRORE +  "exit" + RESET + "' per terminare.");
        System.out.println("\n------------------------------------< Chat >------------------------------------\n");
        while(!socket.isClosed()){
            try {
                String messaggio = scanner.nextLine();
                if(messaggio.equalsIgnoreCase("exit")){
                    output.write("exit");
                    output.newLine();
                    output.flush();
                    System.out.println("Chiusura chat in corso...");
                    chiudi();
                } else {
                    output.write(colore + nome + RESET + ": " + messaggio);
                    output.newLine();
                    output.flush();
                }
            } catch (IOException e){
                System.err.println(ERRORE + "Errore in scrittura: " + e.getMessage() + RESET);
                chiudi();
            }
        }
    }

    /**
     * Metodo che, mediante un thread, rimane in ascolto di messaggi dal Server e li stampa a video
     */
    public void leggi(){
        new Thread(() -> {
            try {
                while(!socket.isClosed()){
                    String messaggio = input.readLine();
                    if(messaggio == null || messaggio.equalsIgnoreCase("exit")){
                        System.out.println("Il Server ha abbandonato la conversazione. Chiusura chat in corso...");
                        chiudi();
                        break;
                    } else {
                        System.out.println(messaggio);
                    }
                }
            } catch (IOException e) {
                if(!e.getMessage().equals("Socket closed")){
                    System.err.println(ERRORE + "Connessione persa con il Server: " + e.getMessage() + RESET);
                }
                chiudi();
            }
        }).start();
    }

    /**
     * Metodo di chiusura della socket e degli stream
     */
    public synchronized void chiudi(){
        try {
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        } catch (IOException e){
            System.err.println(ERRORE + "Errore nella chiusura delle risorse: " + e.getMessage() + RESET);
        }
        System.exit(0);
    }
}
