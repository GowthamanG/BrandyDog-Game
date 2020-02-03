package bdgame.apps.client.GUI;

import bdgame.network.Protocol;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Cedrik on 06.05.2017.
 */
public class GUIClient extends Application {
    private static Stage loginStage;
    private static Stage lobbyStage;
    private static Stage gameStage;
    private static Stage introStage;
    //Networking
    private static String serverAddress;
    private static String serverPort;
    private static Socket serverSocket;
    private static String nickname;
    //Streams
    private static BufferedReader dataFromServer;
    private static BufferedWriter dataToServer;
    //Controllers
    private static LobbyGUIController lobbyGUIController;
    private static GameController gameController;
    //Window related fields
    private double xOffset = 0;
    private double yOffset = 0;

    public static void initGUIClient(String serverAddress, String serverPort, String nickname) {
        GUIClient.serverAddress = serverAddress;
        GUIClient.serverPort = serverPort;
        GUIClient.nickname = nickname;
        launch();
    }

    /**
     * sets up the connection the server
     *
     * @param infos infos used to connect to a server
     * @returns error message or success message
     */
    static String setUpConnection(String[] infos) {
        Socket newServerSocket = null;
        try {
            if (serverSocket == null)
                serverSocket = new Socket(infos[0], Short.parseShort(infos[1]));
            else {
                newServerSocket = new Socket(infos[0], Short.parseShort(infos[1]));
            }
        } catch (IOException e) {
            return e.getMessage();
        }
        if (newServerSocket != null) {
            messageServer("EXIT;");
            try {
                serverSocket.close();
            } catch (IOException e) {
                return e.getMessage();
            }
            serverSocket = newServerSocket;

        }
        lobbyGUIController.clear();
        return "success";
    }

    /**
     * tries to register on the server with the given nickname
     *
     * @param nickname the nickname to register as
     * @return success message ok or error with a message why it failed
     */
    static String loginOnServer(String nickname) {
        try {
            dataToServer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8));
            dataFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.UTF_8));
            messageServer("JOIN;" + nickname);
            String serverResponse;
            while ((serverResponse = dataFromServer.readLine()) != null) {
                if (serverResponse.equals("JOIN;ok")) {
                    startReceiving();
                    return "success";
                } else if (serverResponse.startsWith("JOIN;ERROR;")) {
                    return serverResponse.split(";")[2];
                }
            }
        } catch (IOException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * creates a background task to receive server messages
     */
    private static void startReceiving() {
        Task receivingTask = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    String serverMessage;
                    while ((serverMessage = dataFromServer.readLine()) != null) {
                        processProtocol(serverMessage);
                    }
                } catch (IOException e) {
                    if (e instanceof SocketException) {

                        System.out.println("Socket to Server closed.");
                    } else {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        new Thread(receivingTask).start();
    }

    static Stage getLoginStage() {
        return loginStage;
    }

    static Stage getLobbyStage() {
        return lobbyStage;
    }

    static Stage getGameStage() {
        return gameStage;
    }

    static Stage getIntroStage() {
        return introStage;
    }

    static void messageServer(String msg) {
        try {
            dataToServer.write(msg);
            dataToServer.newLine();
            dataToServer.flush();
        } catch (IOException e) {
            System.out.println("Writing to Socket failed.");
        }
    }

    /**
     * Parsing the received server message by using the protocol
     *
     * @param msg the server message
     */
    private static void processProtocol(String msg) {
        String[] serverMessage = msg.split(";");
        if (serverMessage[0].equals("GAME")) {
            switch (Protocol.GameProtocol.valueOf(serverMessage[1])) {
                case START:
                    lobbyGUIController.openGameWindow();
                    gameController.setOwnColor(serverMessage[2]);
                    gameController.setGameRunning();
                    break;
                case GET:
                    gameController.addCardToHand(serverMessage[2]);
                    break;
                case PLAY:
                    if (serverMessage.length == 4) {
                        if (serverMessage[2].equals("ERROR")) {
                            gameController.showError(serverMessage[3]);
                        }
                    }
                    break;
                case SET:
                    if (serverMessage.length == 4) {
                        switch (serverMessage[2]) {
                            case "CARD":
                                gameController.setLastPlayedCard(serverMessage[3]);
                                break;
                            case "HAND":
                                if (serverMessage[3].equals("EMPTY")) {
                                    gameController.clearHand();
                                }
                                break;
                            default:
                                gameController.setTokenPosition(serverMessage[2], serverMessage[3]);
                                break;
                        }
                    }
                    if (serverMessage.length == 5) {
                        if (serverMessage[2].equals("TURN")) {
                            gameController.setTurn(serverMessage[4], serverMessage[3]);
                        } else if (serverMessage[2].equals("CARD")) {
                            if (serverMessage[3].equals("REMOVE")) {
                                gameController.removeCardFromHand(serverMessage[4]);
                            }
                        }
                    }
                    break;
                case FINISH:
                    gameController.setGameEnd(serverMessage[2], serverMessage[3]);
                    break;
                case EXIT:
                    gameController.showInfo(serverMessage[2] + " surrenderd and left the Game.");
                    break;
            }
        } else {
            switch (Protocol.ChatProtocol.valueOf(serverMessage[0])) {
                case CHAT:
                    switch (serverMessage[1]) {
                        case "ALL":
                            if (serverMessage.length > 4) {
                                lobbyGUIController.appendServerMessageArea(serverMessage[2] + ": " + rebuildMessage(serverMessage, 3));
                            } else
                                lobbyGUIController.appendServerMessageArea(serverMessage[2] + ": " + serverMessage[3]);
                            break;
                        case "SERVER":
                            if (serverMessage.length > 3) {
                                lobbyGUIController.appendServerMessageArea(serverMessage[1] + ": " + rebuildMessage(serverMessage, 2));
                            } else
                                lobbyGUIController.appendServerMessageArea(serverMessage[1] + ": " + serverMessage[2]);
                            break;
                        case "PRIVATE":
                            if (serverMessage.length > 4) {
                                lobbyGUIController.privateMessageReceived(serverMessage[2] + ";" + rebuildMessage(serverMessage, 3));
                            } else
                                lobbyGUIController.privateMessageReceived(serverMessage[2] + ";" + serverMessage[3]);
                            break;
                        case "LOBBY":
                            if (serverMessage[2].equals("SERVER")) {
                                lobbyGUIController.lobbyMessageReceived(serverMessage[2], serverMessage[3]);
                            } else {
                                if (serverMessage.length > 3) {
                                    lobbyGUIController.lobbyMessageReceived("", rebuildMessage(serverMessage, 2));
                                } else
                                    lobbyGUIController.lobbyMessageReceived("", serverMessage[2]);
                            }
                            break;
                    }
                    break;
                case SHOW:
                    switch (serverMessage[1]) {
                        case "CLIENTS":
                            HashSet<String> clients = new HashSet<>();
                            clients.addAll(Arrays.asList(serverMessage).subList(2, serverMessage.length));
                            lobbyGUIController.updateClientList(clients);
                            break;
                        case "SCOREBOARD":
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 2; i < serverMessage.length; i++) {
                                stringBuilder.append(serverMessage[i]);
                                stringBuilder.append("\n");
                            }
                            lobbyGUIController.updateScoreboard(stringBuilder.toString());
                            break;
                        case "LOBBIES":
                            if (serverMessage.length > 2) {
                                StringBuilder stringBuilder1 = new StringBuilder();
                                for (int i = 2; i < serverMessage.length; i++) {
                                    stringBuilder1.append(serverMessage[i]);
                                    stringBuilder1.append(";");
                                }
                                lobbyGUIController.updateLobbyList(stringBuilder1.toString());
                            } else {
                                lobbyGUIController.updateLobbyList("");
                            }
                            break;

                    }
                    break;
                case NICK:
                    if (serverMessage[1].equals("ERROR")) {
                        lobbyGUIController.createErrorAlert(serverMessage[2]);
                    }
                    if (serverMessage.length == 3) {
                        lobbyGUIController.updateNicknames(serverMessage[1], serverMessage[2]);
                    }
                    break;
                case ENTER:
                    if (serverMessage.length == 1) {
                        lobbyGUIController.createErrorAlert("Already in specified lobby.");
                    } else if (serverMessage[1].equals("ERROR")) {
                        lobbyGUIController.createErrorAlert(serverMessage[2]);
                    } else {
                        lobbyGUIController.enterLobby(serverMessage[1]);
                    }
                    break;
                case READY:
                    if (serverMessage[1].equals("PLAYING")) {
                        lobbyGUIController.toggleReadyLock();
                    }
                    break;
            }
        }
    }

    /**
     * clean up after a game
     *
     * @param surrender specifies if the game was surrendered
     */
    static void closedGame(boolean surrender) {
        lobbyGUIController.resetReadyButton();
        if (surrender) {
            lobbyGUIController.leaveLobby();
        }
        gameController.muteSounds();
        gameController.initUI();
        gameStage.close();
    }

    static void unmuteGame() {
        gameController.unmuteSounds();
    }

    static String rebuildMessage(String[] serverMessage, int index) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = index; i < serverMessage.length; i++) {
            stringBuilder.append(serverMessage[i]);
            stringBuilder.append(";");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    static String getServerInfo() {
        return serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort() + ":" + serverSocket.getInetAddress().getHostName();
    }

    static void requestClientList() {
        messageServer("SHOW;CLIENTS");
    }

    static void requestLobbyList() {
        messageServer("SHOW;LOBBIES");
    }

    static void requestScoreboard() {
        messageServer("SHOW;SCOREBOARD");
    }

    static void requestNicknameChange(String newNickname) {
        messageServer("NICK;" + newNickname);
    }

    static void enterLobby(String lobbyName) {
        messageServer("ENTER;" + lobbyName);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            introStage = primaryStage;
            prepareLogin();
            prepareLobby();
            prepareGameUI();
            showGameIntro();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * fades in the login screen
     */
    private void showLogin() {
        loginStage.show();
        FadeTransition ft = new FadeTransition(Duration.millis(1500), loginStage.getScene().getRoot());
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    /**
     * creates a stage to show the skippable intro sequence
     *
     * @throws Exception
     */
    private void showGameIntro() throws Exception {
        String introURL = getClass().getResource("/videos/intro.mp4").toExternalForm();
        Media intro = new Media(introURL);
        MediaPlayer mediaPlayer = new MediaPlayer(intro);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setVolume(1.0);
        MediaView view = new MediaView(mediaPlayer);
        view.setFitHeight(720 / 2);
        view.setFitWidth(1280 / 2);
        AnchorPane root = new AnchorPane(view);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.ESCAPE) || event.getCode().equals(KeyCode.SPACE)) {
                mediaPlayer.stop();
            }
        });
        scene.setFill(Color.TRANSPARENT);
        introStage.setScene(scene);
        introStage.initStyle(StageStyle.TRANSPARENT);
        introStage.show();
        mediaPlayer.setOnStopped(this::closeIntro);
        mediaPlayer.setOnEndOfMedia(this::closeIntro);
    }

    /**
     * closing the intro and starting the transition to the login screen
     */
    private void closeIntro() {
        FadeTransition ft = new FadeTransition(Duration.millis(1500), introStage.getScene().getRoot());
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        introStage.hide();
        showLogin();
    }

    /**
     * prepares the lobby screen already at start
     *
     * @throws Exception
     */
    private void prepareLobby() throws Exception {
        lobbyStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/LobbyGUI.fxml"));
        Parent root = loader.load();
        lobbyGUIController = loader.getController();
        lobbyStage.initStyle(StageStyle.TRANSPARENT);
        lobbyStage.setTitle("BDGame Login");
        Scene lobbyScene = new Scene(root);
        lobbyScene.setFill(Color.TRANSPARENT);
        lobbyScene.setRoot(root);
        lobbyStage.setScene(lobbyScene);
        lobbyStage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                }
        );
    }

    /**
     * prepares the game upon starting the application
     *
     * @throws Exception
     */
    private void prepareGameUI() throws Exception {
        gameStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/GameUI.fxml"));
        Parent root = loader.load();
        gameController = loader.getController();
        gameStage.initStyle(StageStyle.UNDECORATED);
        gameStage.setTitle("BDGame");
        Scene gameScene = new Scene(root);
        gameScene.setRoot(root);
        gameStage.setScene(gameScene);
        gameStage.setOnShown(arg0 -> gameController.initUI());
        gameStage.setOnCloseRequest(e -> Platform.exit());
    }

    /**
     * prepares the login screen stage
     */
    private void prepareLogin() throws Exception {
        loginStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/LoginGUI.fxml"));
        Parent root = loader.load();
        LoginGUIController loginGUIController = loader.getController();


        // make login window movable
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            loginStage.setX(event.getScreenX() - xOffset);
            loginStage.setY(event.getScreenY() - yOffset);
        });

        loginStage.initStyle(StageStyle.TRANSPARENT);
        loginStage.setTitle("BDGame Login");
        Scene loginScene = new Scene(root);
        loginScene.setFill(Color.TRANSPARENT);
        loginScene.setRoot(root);
        loginStage.setScene(loginScene);

        //Controller access
        loginGUIController.setServerPortField(serverPort);
        loginGUIController.setServerAddressField(serverAddress);
        loginGUIController.setNicknameField(nickname);
    }
}
