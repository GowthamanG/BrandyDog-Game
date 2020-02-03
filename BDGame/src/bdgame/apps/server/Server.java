package bdgame.apps.server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class Server {

    private static ServerSocket serverSocket;
    private static HashMap<String, ClientThread> clients = new HashMap<>();
    private static HashMap<String, HashSet<String>> lobbies = new HashMap<>();
    private static Highscore highscore = new Highscore();

    /**
     * Creates a serversocket on the specified port
     *
     * @param ListenPort port to listen to
     */
    public Server(int ListenPort) {
        try {
            serverSocket = new ServerSocket(ListenPort);
        } catch (IOException e) {
            if (e instanceof BindException) {
                System.out.println("Port is already in use on this machine.");
            }
        }
        start();
    }

    /**
     * accepts incoming connections and starts a client thread for the client which connected
     */
    private void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread thread = new ClientThread(clientSocket, clients, lobbies, highscore);
                thread.start();
                System.out.println(clientSocket.getInetAddress() + " connected.");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
