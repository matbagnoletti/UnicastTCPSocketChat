package chat.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private final String nome;
    private final String colore;
    private final String RESET = "\033[0m";
    private Socket socket;
    /**
     * stream di output verso il Server
     */
    private BufferedWriter output;
    /**
     * stream di input dal Server
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

            output.write(colore + nome + RESET + " si è unito alla chat!");
            output.newLine();
            output.flush();

            leggi();
            scrivi();
        } catch (UnknownHostException e){
            System.out.println("Errore nella connessione al Server: controlla di aver inserito un indirizzo IP valido");
            chiudi();
        } catch (IOException e){
            System.err.println("Errore nella creazione della socket con il Server: " + e.getMessage());
            chiudi();
        }
    }

    /**
     * metodo per la lettura da tastiera dei messaggi e l'invio al Server
     */
    public void scrivi(){
        System.out.println("Client pronto all'invio di messaggi! Digita 'exit' per terminare.");
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
                System.err.println("Errore in scrittura: " + e.getMessage());
                chiudi();
            }
        }
    }

    /**
     * metodo che, mediante un thread, rimane in ascolto di messaggi dal Server e li stampa a video
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
                    System.err.println("Si è verificato un errore con lo stream di input (lettura): " + e.getMessage());
                }
                chiudi();
            }
        }).start();
    }

    /**
     * metodo di chiusura della socket e degli stream
     */
    public void chiudi(){
        try {
            if(socket != null && !socket.isClosed()){
                socket.close();
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
}
