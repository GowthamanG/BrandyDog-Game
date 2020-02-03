package bdgame.apps.server;

import bdgame.network.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;


/**
 * ClientThread is used to handle the communication with the client.
 */
class ClientThread extends Thread {

    //Other
    private final String illegalCharacters = ";,:=";
    //Lists
    private HashMap<String, HashSet<String>> serverLobbies;
    private HashMap<String, ClientThread> allClients;
    //Network
    private Socket socket = null;
    private InetAddress clientIP;
    private long lastPongReceived;
    private long lastCommandReceived;
    private long lastPingSent;
    //Client Information
    private String clientName;
    private String clientLobby;
    private boolean isReady;
    private boolean isPlaying;
    //Communication
    private BufferedWriter dataToClient;
    //Game
    private volatile Highscore highscore;
    private volatile GameThread gameThread;


    /**
     * Contructor for the ClientThread which is used to handle the communication with the client.
     *
     * @param socket        The socket of the established connection to the client.
     * @param allClients    A HashMap of all Clients currently connected to the Server (Name -> ClientThread)
     * @param serverLobbies A HashMap of all Lobbies currently open on the Server.
     * @param highscore     The highscorelist which was created or opened from the server.
     */
    ClientThread(Socket socket, HashMap<String, ClientThread> allClients, HashMap<String, HashSet<String>> serverLobbies, Highscore highscore) {
        super("ClientThread");
        this.socket = socket;
        this.serverLobbies = serverLobbies;
        this.allClients = allClients;
        this.highscore = highscore;
        this.clientLobby = "";
    }

    /**
     * The run method contains the loop which is listening for client messages
     * also sets up the reader, writer and initial variables
     */
    public void run() {
        try {
            // setting up
            String clientMessage;
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), "UTF-8"));
            dataToClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            clientIP = socket.getInetAddress();
            isReady = false;


            boolean reachable = true;
            while (reachable) {
                if (inputFromClient.ready()) {
                    clientMessage = inputFromClient.readLine();
                    processProtocol(clientMessage);
                    lastCommandReceived = System.currentTimeMillis();
                }
            }
            inputFromClient.close();
            dataToClient.close();
            closeConnection();

        } catch (IOException e) {
            if (e instanceof SocketException)
                System.out.println(clientIP + " closed connection.");
            else
                e.printStackTrace();
        }
    }

    /**
     * Function to notify the running gamethread from within the clienthread
     */
    void notifyGame() {
        synchronized (gameThread) {
            gameThread.notify();
        }
    }


    /**
     * processProtocol is a method with a huge switch tree to parse the incoming client messages.
     *
     * @param msg the clients message
     */
    private void processProtocol(String msg) {
        String[] clientMessage = msg.split(";");
        if (clientMessage[0].equals("GAME")) {
            switch (Protocol.GameProtocol.valueOf(clientMessage[1])) {
                case EXIT:
                    if (isPlaying) {
                        if (clientMessage.length > 2) {
                            gameThread.playerSurrenders(clientName);
                            if (gameThread.retrievePlayerAction(clientMessage[2] + ";SURRENDER")) {
                                notifyGame();
                            }
                        }
                    }
                    exitGameAndLobby(false);
                    break;
                case PLAY:
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 2; i < clientMessage.length; i++) {
                        stringBuilder.append(clientMessage[i]);
                        stringBuilder.append(";");
                    }
                    if (gameThread.retrievePlayerAction(stringBuilder.toString())) {
                        notifyGame();
                    } else {
                        receiveMessage("GAME;PLAY;ERROR;Not your turn.");
                    }

                    break;

            }

        } else {
            switch (Protocol.ChatProtocol.valueOf(clientMessage[0])) {
                case JOIN:
                    if (clientMessage.length > 2) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 1; i < clientMessage.length; i++) {
                            stringBuilder.append(clientMessage[i]);
                            stringBuilder.append(";");
                        }
                        stringBuilder.setLength(stringBuilder.length() - 1);
                        register(stringBuilder.toString());
                    } else
                        register(clientMessage[1]);
                    break;
                case SHOW:
                    if (clientMessage.length == 1)
                        updateClient("", this);
                    else
                        updateClient(clientMessage[1], this);
                    break;
                case EXIT:
                    try {
                        closeConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case READY:
                    toggleReady();
                    break;
                case NICK:
                    if (clientMessage.length > 2) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 1; i < clientMessage.length; i++) {
                            stringBuilder.append(clientMessage[i]);
                            stringBuilder.append(";");
                        }
                        stringBuilder.setLength(stringBuilder.length() - 1);
                        changeNickname(stringBuilder.toString());
                    } else
                        changeNickname(clientMessage[1]);
                    break;
                case ENTER:
                    if (clientMessage.length == 1) {
                        break;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    if (clientMessage.length > 2) {
                        for (int i = 1; i < clientMessage.length; i++) {
                            stringBuilder.append(clientMessage[i]);
                            stringBuilder.append(";");
                        }
                        receiveMessage(enterLobby(stringBuilder.toString()));
                    } else {
                        receiveMessage(enterLobby(clientMessage[1]));
                    }
                    updateClients();
                    break;
                case LEAVE:
                    leaveLobby(clientLobby);
                    break;
                case CHAT:
                    switch (clientMessage[1]) {
                        case "ALL":
                            if (clientMessage.length > 3) {
                                broadcast("CHAT;ALL;" + clientName + ";" + rebuildMessage(clientMessage, 2));
                            } else {
                                try {
                                    broadcast("CHAT;ALL;" + clientName + ";" + clientMessage[2]);
                                } catch (ArrayIndexOutOfBoundsException e) {
                                }
                            }
                            break;
                        case "PRIVATE":
                            if (clientMessage.length > 4) {
                                privateMessage(clientMessage[2], rebuildMessage(clientMessage, 3));
                            } else
                                try {
                                    privateMessage(clientMessage[2], clientMessage[3]);
                                } catch (ArrayIndexOutOfBoundsException e) {
                                }
                            break;
                        case "LOBBY":
                            if (clientMessage.length > 4) {
                                messageLobby(clientName + ": " + rebuildMessage(clientMessage, 3), clientMessage[2]);
                            } else {
                                try {
                                    messageLobby(clientName + ": " + clientMessage[3], clientMessage[2]);
                                } catch (ArrayIndexOutOfBoundsException e) {
                                }
                            }
                            break;
                    }
                    break;
            }
            updateClients(); //always call the update function after parsing a client command.
        }
    }


    /**
     * rebuildMessage is helper function to prevent information loss when the client message contained ';'
     *
     * @param clientMessage the clientmessage itself as a stringArray
     * @param index         the index where the actual message content starts
     * @return
     */
    private String rebuildMessage(String[] clientMessage, int index) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = index; i < clientMessage.length; i++) {
            stringBuilder.append(clientMessage[i]);
            stringBuilder.append(";");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * Handles the enter lobby request from a client:
     *
     * @param lobbyName
     * @return returns either a confirmation message or an error message
     */
    private String enterLobby(String lobbyName) {

        if (lobbyName.equals(clientLobby)) {
            return "ENTER;";
        }

        //check if ready
        if (isReady) {
            return "ENTER;ERROR;You are ready in other lobby.";
        }

        // check if destination lobby is playing
        if (isLobbyReady(lobbyName)) {
            return "ENTER;ERROR;Lobby is already playing a game.";
        }

        //exit old lobby
        if (clientLobby != null && !clientLobby.isEmpty()) {
            leaveLobby(clientLobby);
        }

        if (serverLobbies.containsKey(lobbyName)) {
            if (serverLobbies.get(lobbyName).size() < 4) {
                serverLobbies.get(lobbyName).add(clientName);
                messageLobby("SERVER;" + clientName + " entered Lobby.", lobbyName);
                clientLobby = lobbyName;
                return "ENTER;" + lobbyName + ";";
            } else {
                return "ENTER;ERROR;Lobby '" + lobbyName + "' is full.";
            }
        } else {
            if (checkNameSymbols(lobbyName).equals("ok")) {
                serverLobbies.put(lobbyName, new HashSet<>());
                serverLobbies.get(lobbyName).add(clientName);
                clientLobby = lobbyName;
                return "ENTER;" + lobbyName + ";";
            } else {
                return "ENTER;" + checkNameSymbols(lobbyName);
            }
        }
    }

    /**
     * Processes client request to leave the lobby and handles deletion of client entries in lookup maps
     *
     * @param lobbyName the lobby the client wants to leave
     */
    private void leaveLobby(String lobbyName) {
        if (serverLobbies.containsKey(lobbyName)) {
            if (serverLobbies.get(lobbyName).contains(clientName)) {
                serverLobbies.get(lobbyName).remove(clientName);
                messageLobby("SERVER;" + clientName + " left the lobby.", lobbyName);
                clientLobby = "";
                if (serverLobbies.get(lobbyName).size() == 0) {
                    serverLobbies.remove(lobbyName);
                }
                //following errors did not show up and should never actually:
            } else {
                System.out.println("Error leavingLobby: " + lobbyName + " -> " + clientName);
            }
        } else {
            if (!lobbyName.isEmpty())
                System.out.println("Error leavingLobby: " + lobbyName + " -> " + clientName);
        }
    }

    /**
     * Messages all clients in the specified lobby, uses lobby chat prefix
     *
     * @param msg       the message to be sent to the client in the lobby
     * @param lobbyName the name of the lobby
     */
    private void messageLobby(String msg, String lobbyName) {
        if (serverLobbies.containsKey(lobbyName)) {
            for (String client : serverLobbies.get(lobbyName)) {
                allClients.get(client).receiveMessage("CHAT;LOBBY;" + msg);
            }
        }
    }

    /**
     * updateClients will call updateClient to send information
     * about the clients connected and the open lobbies on the server
     */
    private void updateClients() {
        for (ClientThread clients : allClients.values()) {
            updateClient("CLIENTS", clients);
            updateClient("LOBBIES", clients);
        }
    }

    /**
     * updateClient is used to send information the client, which are defined as mode.
     *
     * @param mode   three modes: SCOREBOARD, CLIENTS, LOBBIES if none: all
     * @param client the client to be updated
     */
    private void updateClient(String mode, ClientThread client) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (mode) {
            case "SCOREBOARD":
                stringBuilder.append("SCOREBOARD;");
                stringBuilder.append(highscore.getScoreboard());
                break;
            case "CLIENTS":
                stringBuilder.append("CLIENTS;");
                stringBuilder.append(client.getAllClients());
                break;
            case "LOBBIES":
                stringBuilder.append("LOBBIES;");
                stringBuilder.append(client.getAllLobbies());
                break;
            default:
                updateClient("HIGHSCORE", client);
                updateClient("CLIENTS", client);
                updateClient("LOBBIES", client);
                break;
        }
        if (!stringBuilder.toString().isEmpty())
            client.receiveMessage("SHOW;" + stringBuilder.toString());

    }

    /**
     * Tries to client as user specified by name, will call check methods and automatically fix duplicate names
     *
     * @param name the name the client wants to be registered as
     */
    private void register(String name) {
        name = checkName(name);
        if (name.startsWith("ERROR;")) {
            receiveMessage("JOIN;" + name);
        } else {
            receiveMessage("JOIN;ok");
            receiveMessage("CHAT;SERVER;Logged in as " + name);
            clientName = name;
            broadcast("CHAT;SERVER;" + clientName + " just joined the Server.");
            clientLobby = "";
            allClients.put(clientName, this);
            updateClients();
        }
    }

    /**
     * Similiar to register, this method is used to change the name of the client, also checking for duplicates or
     * an invalid name
     *
     * @param name the name the client wants to change to
     */
    private void changeNickname(String name) {
        name = checkName(name);
        if (name.startsWith("ERROR;")) {
            receiveMessage("NICK;" + name);
            return;
        } else {
            broadcast("CHAT;SERVER;" + clientName + " changed his name to: " + name + ".");
            broadcast("NICK;" + clientName + ";" + name);
            removeClient(clientName);
            clientName = name;
            addClient(clientName, clientLobby);
        }
        updateClients();
    }

    /**
     * Adds client to the client and lobbies hashmap, so he can be found on the server.
     * usually called by register or changeNickname
     *
     * @param clientName the clients name
     * @param lobbyName  the lobby he is being added to, empty for no lobby
     */
    private void addClient(String clientName, String lobbyName) {
        allClients.put(clientName, this);
        if (lobbyName != null && !lobbyName.isEmpty())
            serverLobbies.get(lobbyName).add(clientName);
    }

    /**
     * Removes specified client from all hashmaps he is in, it calls leavelobby to
     * remove the client from the lobby he is currently in
     *
     * @param clientName the clients name
     */
    private void removeClient(String clientName) {
        if (allClients.containsKey(clientName)) {
            leaveLobby(allClients.get(clientName).getClientLobby());
            allClients.remove(clientName);
        }
    }

    /**
     * Getter for the name of the lobby the client is in
     *
     * @return clientLobby as a String
     */
    private String getClientLobby() {
        return clientLobby;
    }

    public String getClientName() {
        return clientName;
    }


    /**
     * Checks name for duplicates and by using symbolcheck also for invalid characters
     *
     * @param name the name to be checked
     * @return
     */
    private String checkName(String name) {
        String symbolCheck;
        if ((symbolCheck = checkNameSymbols(name)).startsWith("ERROR;")) {
            return symbolCheck;
        }
        int i = 1;
        String originalName = name;
        boolean isValid = false;
        while (!isValid) {
            isValid = true;
            for (String clientNames : allClients.keySet()) {
                if (clientNames.equals(name)) {
                    name = originalName + "_" + i;
                    i++;
                    isValid = false;
                }
            }
        }
        return name;
    }

    /**
     * Checks name for invalid characters (defined as a field: illegalCharacters)
     *
     * @param name the name to be checked
     * @return either "ok" or "ERROR;Name contains illegal characters."
     */
    private String checkNameSymbols(String name) {
        name = name.replaceAll(" ", "");
        if (name.matches(".*[" + illegalCharacters + "].*")) {
            return "ERROR;Name contains illegal characters.";
        }
        return "ok";
    }

    /**
     * Iterates through the client map and creates a string containing all client names
     *
     * @return all clients in one string, seperated by ';'
     */
    private String getAllClients() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String name : allClients.keySet()) {
            if (!name.equals(clientName)) {
                stringBuilder.append(name);
                stringBuilder.append(";");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Iterates through the serverLobbies map and creates a string containing all lobbies and the clients in it
     *
     * @return all lobbies in one string, seperated by ';' with their clients seperated by ":"
     */
    private String getAllLobbies() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String lobby : serverLobbies.keySet()) {
            stringBuilder.append(lobby);
            stringBuilder.append(":");
            for (String clientInLobby : serverLobbies.get(lobby)) {
                stringBuilder.append(clientInLobby);
                stringBuilder.append(":");
            }
            stringBuilder.append(";");
        }
        return stringBuilder.toString();
    }


    /**
     * Checks if all clients in a lobby are ready by iterating through the playerlist of the lobby
     * and checking for each client
     *
     * @param lobbyName name of the lobby to be checked
     * @return either false or true
     */
    private boolean isLobbyReady(String lobbyName) {
        if (serverLobbies.containsKey(lobbyName)) {
            boolean lobbyReady = true;
            for (String client : serverLobbies.get(lobbyName)) {
                if (!allClients.get(client).isReady()) {
                    lobbyReady = false;
                }
            }
            return serverLobbies.get(lobbyName).size() > 1 && lobbyReady;
        } else {
            return false;
        }
    }

    /**
     * Sets game related boolean to not playing and is leaving the lobby if specified
     *
     * @param exitLobby boolean to set if the lobby is to be left
     */
    void exitGameAndLobby(boolean exitLobby) {
        isPlaying = false;
        isReady = false;
        if (exitLobby)
            leaveLobby(clientLobby);
    }


    /**
     * Toggles the clients ready state, also:
     * calls a ready check for the whole lobby and if ready calls startGame()
     */
    private void toggleReady() {
        if (!isPlaying) {
            isReady = !isReady;
            if (isReady) {
                messageLobby("SERVER;" + clientName + " is ready.", clientLobby);
                if (isLobbyReady(clientLobby)) {

                    messageLobby("SERVER;Everyone ready. Starting game...", clientLobby);
                    startGame(serverLobbies.get(clientLobby));

                }
            } else {
                messageLobby("SERVER;" + clientName + " is not ready anymore.", clientLobby);
            }
        } else {
            receiveMessage("READY;PLAYING");
        }
    }


    /**
     * Closes the connection for the client and informs other clients about it
     *
     * @throws IOException if socket was already closed
     */
    private void closeConnection() throws IOException {
        removeClient(clientName);
        broadcast("CHAT;SERVER;" + clientName + " left the Server.");
        socket.close();
        System.out.println(clientIP + " closed connection.");
    }

    private boolean isReady() {
        return isReady;
    }


    /**
     * Sends a string message through a writer to the client
     *
     * @param msg the message to send to the client
     */
    void receiveMessage(String msg) {
        try {
            dataToClient.write(msg);
            dataToClient.newLine();
            dataToClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * messages all clients on the server
     *
     * @param msg the message to be sent
     */
    private void broadcast(String msg) {
        for (String clients : allClients.keySet()) {
            allClients.get(clients).receiveMessage(msg);
        }
    }


    /**
     * Messages a specified user by the client and sends confirmation or error to client
     *
     * @param receiver user to be messaged
     * @param msg      the message
     */
    private void privateMessage(String receiver, String msg) {
        boolean messageSent = false;


        for (String clients : allClients.keySet()) {
            if (clients.equals(receiver)) {
                allClients.get(clients).receiveMessage("CHAT;PRIVATE;" + clientName + ";" + msg);
                receiveMessage("CHAT;PRIVATE;=>;" + msg);
                messageSent = true;
            }
        }

        if (!messageSent) {
            receiveMessage("CHAT;ERROR;User '" + receiver + "' is not on this server.");
        }
    }

    /**
     * Starts the game with the specified set of players
     *
     * @param players players to start the game for
     */
    private void startGame(HashSet<String> players) {
        HashMap<String, ClientThread> playerMap = new HashMap<>();
        for (String player : players) {
            playerMap.put(player, allClients.get(player));
        }
        GameThread game = new GameThread(playerMap, highscore);
        for (ClientThread client : playerMap.values()) {
            client.setGameThread(game);
            client.isPlaying = true;
        }
        receiveMessage("READY;PLAYING");
    }

    private void setGameThread(GameThread gameThread) {
        this.gameThread = gameThread;
    }
}
