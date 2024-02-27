package org.tpsit;

import java.io.*;
import java.net.Socket;
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
        } catch (IOException e){
            System.err.println("Errore nella connessione al Server! Verifica di aver inserito un corretto indirizzo IP e numero di porta, e che il Server sia in ascolto: " + e.getMessage());
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
            while(!socket.isClosed()){
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
        System.out.print("Inserisci la porta del Server: ");
        int porta = Integer.parseInt(scanner.nextLine());
        client.connetti(ip, porta);
        client.leggi();
        client.scrivi();
    }
}
