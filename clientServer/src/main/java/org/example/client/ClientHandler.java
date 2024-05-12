package org.example.client;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

// Runnable is responsible for making instances run on a separate thread
public class ClientHandler implements Runnable {

    /**  To keep track of the every ClientHandler object that we have instantiated, we need to keep a static ArrayList record 
     * using 'static' because we want the Array to belong to the class and not every instance that will be created.
    */
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;


    public ClientHandler(Socket socket) { 
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("SERVER: " + clientHandlers + " has entered joined the server");
        } catch (IOException e) {
            // closeEverything() will close streams and socket
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        
    }

    @Override
    public void run() {
        String messageFromClient;

        try {
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();
                broadCastMessage(messageFromClient);
            } 
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedReader);
            break;
        }
    }

    public void broadCastMessage(String messageToSend) {
        for (ClientHandler clientHandler: clientHandlers) {
            try {
                if (!this.clientUserName.equals(clientUserName)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    /** Because on the client, they'll be waiting on a new line, because they'll be using readLine(),
                     * we need to explicitly send a new line character.
                     * Basically, what the newLine() method does is tell the client that, I'm done sending data, no need 
                     * to wait for anymore data.
                     */
                    clientHandler.bufferedWriter.newLine();
                    /**
                     * A BufferedWriter will not be sent out unless it's full 
                     *  The messages/data we are sending aren't big enough to fill the entire Buffer so we need to manually flush it */
                    clientHandler.bufferedWriter.flush();
                    
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    // Signals that user has left the chat. It will display for users still in the user that a user has left the server.
    public void removeClientHandler() {
        clientHandlers.remove(this);
        System.out.println("SERVER: " + clientUserName + " has left the server.");
    }

    // Used to close down client-server connection and our streams
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {

            /** NOTE: 
             * With streams, you only need to close the wrapper as the underlying streams are closed when you close the wrapper 
             * So, closing bufferedReader, bufferedWriter does the job*/ 
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
