<h1 align="center">UnicastTCPSocketChat</h1>

<p align="center" style="font-family: monospace">Made by <a href="https://github.com/matbagnoletti">@matbagnoletti</a></p>
<p align="center" style="font-family: monospace">Docenti: prof.ssa <a href="https://github.com/mciuchetti">@mciuchetti</a> e prof.ssa Fioroni</p>
<p align="center" style="font-family: monospace">Corso TPSIT a.s. 2023/2024, <a href="https://www.avoltapg.edu.it/">ITTS A. Volta (PG)</a></p>
<p align="center">
    <img src="https://img.shields.io/github/last-commit/matbagnoletti/UnicastTCPSocketChat?style=for-the-badge" alt="Ultimo commit">
    <img src="https://img.shields.io/github/languages/top/matbagnoletti/UnicastTCPSocketChat?style=for-the-badge" alt="Linguaggio">
</p>

## Descrizione
Applicazione Java che utilizza le socket per implementare una comunicazione TCP unicast tra Client e Server.

## Requisiti
- [JDK](https://www.oracle.com/it/java/technologies/downloads/) (v8 o superiore) installato.
- [Gradle](https://gradle.org/install/) (v8.6) installato.

È possibile visualizzare le versioni già presenti sul proprio dispositivo mediante i seguenti comandi:
```
java --version
gradle --version
```

## Installazione e utilizzo
1. Scaricare il file compresso del progetto
2. Estrarre il progetto
3. Eseguire il Server e il Client.
   - Tramite IDE
   - Tramite terminale:
     1. Naviga nella root del progetto
     2. Esegui il built del progetto: `gradle build`
     3. Identifica il file **jar** nella cartella `/build/libs/`
     3. Esegui il Server: `java -cp build/libs/<nome-del-file-jar>.jar org.tpsit.Server`
     4. Esegui il Client in una finestra: `java -cp build/libs/<nome-del-file-jar>.jar org.tpsit.Client`
     
## Struttura e funzionamento
Il progetto si compone da due classi:
- L'entità [`Client`](src/main/java/org/tpsit/Client.java)
- L'entità [`Server`](src/main/java/org/tpsit/Server.java)

Le due, dopo una configurazione iniziale, permetteranno entrambi all'utente di poter digitare messaggi da inviare all'altra entità e ricevere, contemporaneamente (tramite l'utilizzo di un altro Thread), messaggi che verranno stampati a video.

La chat termina quando uno dei due digita `exit` nella console: in questo caso verranno invocati i rispettivi metodi di chiusura degli stream (`chiudi()`).

Entrambe le classi sono strutturate in modo che, nel caso in cui si verificasse improvvisamente un errore/eccezione (come ad esempio la disconnessione di una delle due entità), il programma, dopo averlo segnalato, invochera i rispettivi metodi di chiusura degli stream (`chiudi()`).

## Licenza d'uso
Questo progetto (e tutte le sue versioni) sono rilasciate sotto la [MB General Copyleft License](LICENSE).