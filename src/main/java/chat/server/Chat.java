package chat.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe di gestione dei Client
 */
public class Chat extends Thread{

    /**
     * Elenco degli utenti connessi al Server
     */
    private static CopyOnWriteArrayList<Chat> utenti = new CopyOnWriteArrayList<>();

    /**
     * La dataSocket derivata dalla connessione del Client al Server
     */
    private final Socket socket;

    /**
     * Lo username del Client
     */
    private final String username;

    /**
     * Colore ASCII del Server
     */
    private static final String COLORE = "\033[35m";

    /**
     * Colore ASCII rosso
     */
    private static final String ERRORE = "\033[31m";

    /**
     * Colore ASCII di reset
     */
    private static final String RESET = "\033[0m";
    
    /**
     * Stream di output verso il Server
     * @see BufferedWriter
     */
    private BufferedWriter output;

    /**
     * Stream di input dal Server
     * @see BufferedReader
     */
    private BufferedReader input;

    /**
     * Costruttore
     * @param socket la dataSocket derivata dalla connessione del Client al Server
     * @param username lo username del Client
     */
    public Chat(Socket socket, String username){
        this.socket = socket;
        this.username = username;
    }
    
    @Override
    public void run(){
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            aggiungiUtente(this);
            leggi();
        } catch (IOException e) {
            System.err.println("Errore nella creazione degli stream");
            chiudi();
        }
    }

    public String getUsername() {
        return username;
    }

    public synchronized void aggiungiUtente(Chat chat){
        if(!utenti.contains(chat)){
            utenti.add(chat);
            System.out.println(COLORE + "[NEW] " + RESET + chat.getUsername() + " si e' unito alla chat!");
        }
    }
    
    public synchronized void rimuoviUtente(Chat chat){
        utenti.remove(chat);
    }

    /**
     * Metodo per l'invio dei messaggi ai Client
     */
    public static synchronized void scrivi(String username, String msg){
        if(!utenti.isEmpty()){
            boolean trovato = false;
            for(Chat chat: utenti){
                if(chat.getUsername().equals(username)){
                    trovato = true;
                    try {
                        chat.output.write(COLORE + "SERVER" + RESET + ": " + msg);
                        chat.output.newLine();
                        chat.output.flush();
                    } catch (IOException e){
                        System.err.println("Errore in scrittura: " + e.getMessage());
                        chat.chiudi();
                    }
                }
            }
            
            if(!trovato){
                System.err.println(ERRORE + "Utente non trovato!" + RESET);
            }
        } else {
            System.err.println(ERRORE + "Nessun utente connesso!" + RESET);
        }
    }

    /**
     * Metodo per la lettura dei messaggi dal Client
     */
    public void leggi(){
        new Thread(() -> {
            try {
                while(!socket.isClosed()){
                    String messaggio = input.readLine();
                    if(messaggio == null || messaggio.equalsIgnoreCase("exit")){
                        System.out.println(username + " ha abbandonato la conversazione.");
                        chiudi();
                        break;
                    } else {
                        System.out.println(messaggio);
                    }
                }
            } catch (IOException e) {
                if(!e.getMessage().equals("Socket closed")){
                    System.err.println(ERRORE + "Si e' verificato un errore con lo stream di input (lettura): " + e.getMessage() + RESET);
                }
                chiudi();
            }
        }).start();
    }

    /**
     * Metodo di chiusura della socket e degli stream
     */
    public void chiudi(){
        try {
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        } catch (IOException e){
            System.err.println(ERRORE + "Errore nella chiusura delle risorse: " + e.getMessage() + RESET);
        }
        rimuoviUtente(this);
    }
}
