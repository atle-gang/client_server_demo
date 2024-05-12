package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.example.client.ClientHandler;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /** Keeps our server running 
     * We want our server to be constantly running until the server socket is closed
    */

    public void startServer() {
        System.out.println("Server running and waiting for client connections...");
        try {
            while (!serverSocket.isClosed()) {
                // Waiting fo the client to connect - read other example I did if I don't remember this blocking method 
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected..");
                /** Each instance of this class, 'Clienthandler', will be responsible for communicating with a client 
                 * This class will also implement the interface Runnable
                 * Runnable is implemented on a class whose instances will be executed by a separate thread - Handle multiple clients
                */
                ClientHandler clientHandler = new ClientHandler(socket);

                // To spawn a new thread, we need to create a Thread object, and then pass in the object that is an instance of the class that implements Runnable
                Thread thread = new Thread(clientHandler);
                // Use the 'start()' method to begin the execution  of the thread
                thread.start();
            }
        } catch (IOException e) {
           e.printStackTrace(); 
        }
    }

    // Method for handling the errors - it will be used to avoid nested try-catch blocks
    public void closeServerSocket() {
        // To make sure our server socket is not null, because if it is, we will get a NullPointer exception
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        Server server = new Server(serverSocket);
        server.startServer();
    }

}



