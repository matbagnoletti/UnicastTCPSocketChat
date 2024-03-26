package chat.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chat extends Thread{
    private static CopyOnWriteArrayList<Chat> utenti = new CopyOnWriteArrayList<>();
    private final Socket socket;
    private final String username;
    private static final String COLORE = "\033[35m";
    private static final String ERRORE = "\033[31m";
    private static final String RESET = "\033[0m";
    private BufferedReader input;
    private BufferedWriter output;
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
