package org.tpsit;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private final int porta;
    private final String colore = "\033[35m";
    private final String RESET = "\033[0m";
    /**
     * stream di output verso il Client
     */
    private BufferedWriter output;
    /**
     * stream di input dal Client
     */
    private BufferedReader input;
    private final Scanner scanner;
    public Server(int porta){
        this.porta = porta;
        this.scanner = new Scanner(System.in);
        try {
            serverSocket = new ServerSocket(this.porta);
            System.out.println("Server avviato e in ascolto sulla porta " + porta + "!");
            this.clientSocket = attendi();

            if(clientSocket != null){
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String connessione = input.readLine();
                System.out.println(connessione);
            } else {
                System.err.println("Impossibile creare la socket con il Client!");
                chiudi();
            }

        } catch (IOException e){
            System.err.println("Errore nell'apertura del Server: " + e.getMessage());
            chiudi();
        }
    }

    /**
     * metodo per l'accettazione della connessione con il Client e l'apertura degli stream
     * @return la socket con il Client
     */
    public Socket attendi(){
        try {
            return serverSocket.accept();
        } catch (IOException e){
            System.out.println("Errore nell'attesa di connessioni: " + e.getMessage());
            return null;
        }
    }

    /**
     * metodo per la lettura da tastiera dei messaggi e l'invio al Client
     */
    public void scrivi(){
        System.out.println("Server pronto all'invio di messaggi! Digita 'exit' per terminare.");
        while(!clientSocket.isClosed() && !serverSocket.isClosed()){
            try {
                String messaggio = scanner.nextLine();
                output.write(colore + "SERVER" + RESET + ": " + messaggio);
                output.newLine();
                output.flush();
                if(messaggio.equalsIgnoreCase("exit")){
                    System.out.println("Chiusura chat in corso...");
                    chiudi();
                }
            } catch (IOException e){
                System.err.println("Errore in scrittura: " + e.getMessage());
                chiudi();
            }
        }
    }

    /**
     * metodo che, mediante un thread, rimane in ascolto di messaggi dal Client e li stampa a video
     */
    public void leggi(){
        new Thread(() -> {
            while(!clientSocket.isClosed() || !serverSocket.isClosed()){
                try {
                    String messaggio = input.readLine();
                    if(messaggio == null || messaggio.equalsIgnoreCase("exit")){
                        System.out.println("Chiusura chat in corso...");
                        chiudi();
                        break;
                    } else {
                        System.out.println(messaggio);
                    }
                } catch (IOException e){
                    System.err.println("Errore in lettura: " + e.getMessage());
                    chiudi();
                }
            }
        }).start();
    }

    /**
     * metodo di chiusura della socket e degli stream
     */
    public void chiudi(){
        try {
            if(clientSocket != null && !clientSocket.isClosed()){
                clientSocket.close();
            }
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
            if(input != null){
                input.close();
            }
            if(output != null){
                output.close();
            }
        } catch (IOException e){
            System.err.println("Errore nella chiusura delle risorse: " + e.getMessage());
        }
        System.exit(0);
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Inserisci il numero di porta in cui avviare il Server: ");
        int porta = Integer.parseInt(scanner.nextLine());

        Server server = new Server(porta);
        server.leggi();
        server.scrivi();
    }
}
