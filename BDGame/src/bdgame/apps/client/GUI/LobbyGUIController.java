package bdgame.apps.client.GUI;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class LobbyGUIController implements Initializable {


    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("[HH:mm:ss]");
    //Offsets to move window
    private double xOffset = 0;
    private double yOffset = 0;
    //Messages
    private HashMap<String, Tab> privateChats;
    private String prefix;
    //Scoreboard
    private TableView scoreboardTable;

    //Lobby
    private String lobbyName;

    //Stage
    private Stage lobbyStage;
    private Stage scoreboardStage;
    private boolean bInitialized;

    @FXML
    private RadioMenuItem gameFullscreen;

    @FXML
    private TabPane chatTabs;

    @FXML
    private TreeView LobbyTreeView;

    @FXML
    private ToggleButton toggleReadyButton;

    @FXML
    private Tab lobbyChatTab;

    @FXML
    private Tab lobbiesTab;

    @FXML
    private MenuItem createLobbyMenuItem;

    @FXML
    private AnchorPane lobbyAnchorPane;

    @FXML
    private ListView<String> UsersList;

    @FXML
    private TextField clientMessageArea;

    @FXML
    private ToggleButton ReadyButton;

    @FXML
    private MenuItem NicknameMenuItem;

    @FXML
    private MenuItem InfoMenuItem;

    @FXML
    private MenuItem CloseMenuItem;

    @FXML
    private MenuItem ScoreboardMenuItem;

    @FXML
    private MenuItem ConnectMenuItem;

    @FXML
    private MenuItem SettingsMenuItem;

    @FXML
    private TextArea serverMessageArea;

    @FXML
    private TextArea lobbyMessageArea;

    @FXML
    private Tab serverChatTab;

    @FXML
    void mouseDragsMenuBar(MouseEvent event) {
        lobbyStage.setX(event.getScreenX() + xOffset);
        lobbyStage.setY(event.getScreenY() + yOffset);
    }

    @FXML
    void mousePressedOnMenuBar(MouseEvent event) {
        xOffset = lobbyStage.getX() - event.getScreenX();
        yOffset = lobbyStage.getY() - event.getScreenY();
    }

    @FXML
    void handleEnterPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            String clientMessage = clientMessageArea.getText();
            if (!clientMessage.isEmpty()) {
                GUIClient.messageServer(prefix + clientMessage.trim());
                clientMessageArea.clear();
            }
        }
    }

    @FXML
    void toggleReady(ActionEvent event) {
        if (toggleReadyButton.isSelected())
            GUIClient.messageServer("READY");
    }

    @FXML
    void clickedConnectItem(ActionEvent event) {
        GUIClient.getLoginStage().show();
    }

    /**
     * opens up dialog to request to create (or enter) a lobby
     *
     * @param event
     */
    @FXML
    void clickedCreateLobbyItem(ActionEvent event) {
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setGraphic(null);
            dialog.setHeaderText("Enter Lobby Name");
            dialog.initOwner(GUIClient.getLobbyStage());

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                lobbyName = result.get();
                if (!lobbyName.isEmpty())
                    GUIClient.enterLobby(lobbyName);
            }
        });
    }

    /**
     * opens up dialog to request nickname change
     *
     * @param event
     */
    @FXML
    void clickedNicknameItem(ActionEvent event) {
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setGraphic(null);
            dialog.setHeaderText("Enter new nickname:");
            dialog.initOwner(GUIClient.getLobbyStage());

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().isEmpty()) {
                GUIClient.requestNicknameChange(result.get());
            }
        });


    }

    @FXML
    void clickedCloseItem(ActionEvent event) {
        GUIClient.messageServer("EXIT");
        GUIClient.getLobbyStage().close();
    }

    /**
     * creates or just presents the scoreboard, functions of the table are disabled
     * click to close
     *
     * @param event
     */
    @FXML
    void clickedScoreboardItem(ActionEvent event) {
        Platform.runLater(() -> {
            GUIClient.requestScoreboard();
            if (scoreboardStage == null) {
                scoreboardStage = new Stage();
                scoreboardStage.initStyle(StageStyle.UNDECORATED);
                scoreboardStage.initOwner(GUIClient.getLobbyStage());
            }
            Scene scene = new Scene(new Group());

            if (scoreboardTable.getColumns().isEmpty()) {
                scoreboardTable.setEditable(false);
                scoreboardTable.setSelectionModel(null);
                TableColumn positionCol = new TableColumn("#");
                positionCol.setCellValueFactory(new PropertyValueFactory<ScoreEntry, Integer>("position"));
                positionCol.setResizable(false);
                positionCol.setStyle("-fx-alignment: CENTER;");
                positionCol.setSortable(false);
                TableColumn playerCol = new TableColumn("Player");
                playerCol.setCellValueFactory(new PropertyValueFactory<ScoreEntry, String>("playerName"));
                playerCol.setResizable(false);
                playerCol.setSortable(false);
                playerCol.setStyle("-fx-alignment: CENTER;");
                TableColumn wonCol = new TableColumn("Won");
                wonCol.setCellValueFactory(new PropertyValueFactory<ScoreEntry, Integer>("timesWon"));
                wonCol.setResizable(false);
                wonCol.setStyle("-fx-alignment: CENTER;");
                wonCol.setSortable(false);
                scoreboardTable.getColumns().addAll(positionCol, playerCol, wonCol);
            }

            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.setPadding(new Insets(10, 0, 0, 10));
            vbox.getChildren().addAll(scoreboardTable);
            ((Group) scene.getRoot()).getChildren().addAll(scoreboardTable);
            scoreboardStage.setScene(scene);
            scoreboardStage.show();

        });
    }

    @FXML
    private void clickedOnLobbyUsageMenu(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            Stage webViewStage = new Stage();
            Scene scene = new Scene(new Group());

            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            String url = getClass().getResource("/documents/lobby.html").toExternalForm();
            webEngine.load(url);
            ((Group) scene.getRoot()).getChildren().addAll(webView);
            webViewStage.setScene(scene);
            webViewStage.initOwner(GUIClient.getLobbyStage());
            webViewStage.show();

        });
    }

    @FXML
    void clickedInfoItem(ActionEvent event) {
        String info = GUIClient.getServerInfo();
        String[] serverInfo = info.split(":");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Server Information");
            alert.setHeaderText(null);
            alert.setContentText(serverInfo[2] + ":\n" + serverInfo[0] + ":" + serverInfo[1]);
            alert.showAndWait();
        });
    }

    @FXML
    void clickedOnServerChatTab(Event event) {
        prefix = "CHAT;ALL;";
    }

    @FXML
    void lobbiesSelected(Event event) {
        if (bInitialized)
            GUIClient.requestLobbyList();
    }

    @FXML
    void usersSelected(Event event) {
        if (bInitialized)
            GUIClient.requestClientList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lobbyStage = GUIClient.getLobbyStage();
        prefix = "CHAT;ALL;";
        privateChats = new HashMap<>();
        scoreboardTable = new TableView();
        scoreboardTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        scoreboardTable.setOnMouseClicked(event -> scoreboardStage.close());
        scoreboardStage = null;
        chatTabs.getTabs().remove(lobbyChatTab);
        chatTabs.getSelectionModel().select(serverChatTab);
        bInitialized = true;


    }

    void appendServerMessageArea(String msg) {
        Platform.runLater(() -> addLineToTextArea(serverMessageArea, msg));
    }

    /**
     * called when receiving a private message:
     * opens a new chat tab for a new private chat tab or retrieves the old one
     * also notifys when not in focus
     *
     * @param msg
     */
    void privateMessageReceived(String msg) {
        Platform.runLater(() -> {
            String[] message = msg.split(";");
            String partner = message[0];
            String clientMessage;
            if (message.length > 2) {
                clientMessage = GUIClient.rebuildMessage(message, 1);
            } else {
                clientMessage = message[1];
            }
            if (partner.equals("=>")) {
                TextArea privateTextArea = (TextArea) chatTabs.getSelectionModel().getSelectedItem().getContent();
                addLineToTextArea(privateTextArea, "You: " + clientMessage);
                return;
            }
            if (privateChats.containsKey(partner)) {
                TextArea privateTextArea = (TextArea) privateChats.get(partner).getContent();
                addLineToTextArea(privateTextArea, partner + ": " + clientMessage);
                if (chatTabs.getSelectionModel().getSelectedItem() != privateChats.get(partner))
                    privateChats.get(partner).setStyle("-fx-border-color:blue;");
                chatTabs.getTabs().add(privateChats.get(partner));
            } else {
                Tab privateTab = new Tab(partner);
                TextArea privateChatArea = new TextArea("Chat started with " + partner + ".\n");
                addLineToTextArea(privateChatArea, partner + ": " + clientMessage);
                privateTab.setContent(privateChatArea);
                privateTab.setStyle("-fx-border-color:blue;");
                privateTab.isClosable();
                privateTab.setOnSelectionChanged(event -> {
                    if (chatTabs.getSelectionModel().getSelectedItem() == privateChats.get(partner)) {
                        privateTab.setStyle(null);
                        prefix = "CHAT;PRIVATE;" + privateTab.getText() + ";";
                        privateChats.get(partner).setStyle(null);
                    }

                });
                privateTab.setOnClosed(event -> chatTabs.getTabs().remove(privateTab));
                chatTabs.getTabs().add(privateTab);
                privateChats.put(partner, privateTab);
            }
        });
    }

    void lobbyMessageReceived(String server, String msg) {
        Platform.runLater(() -> {
            if (server.isEmpty())
                addLineToTextArea(lobbyMessageArea, msg);
            else {
                addLineToTextArea(lobbyMessageArea, "SERVER: " + msg);
            }
        });
    }

    /**
     * updates the client list in the gui
     *
     * @param clients
     */
    void updateClientList(HashSet<String> clients) {
        Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            items.addAll(clients);
            UsersList.setItems(items);
            UsersList.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    if (!UsersList.getSelectionModel().getSelectedItem().isEmpty())
                        startPrivateChat(UsersList.getSelectionModel().getSelectedItem());
                }
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() == 2) {
                        startPrivateChat(UsersList.getSelectionModel().getSelectedItem());
                    }
                }

            });
        });
    }


    void toggleReadyLock() {
        Platform.runLater(() -> toggleReadyButton.setDisable(true));
    }

    void resetReadyButton() {
        toggleReadyButton.setDisable(false);
        toggleReadyButton.setSelected(false);
    }

    /**
     * starts a private chat with the specified partner or retrieves the old existing tab
     *
     * @param partner
     */
    private void startPrivateChat(String partner) {
        Platform.runLater(() -> {
            if (privateChats.containsKey(partner)) {
                chatTabs.getSelectionModel().select(privateChats.get(partner));

            } else {
                TextArea privateChatArea = new TextArea("Chat started with " + partner + ".\n");
                Tab privateTab = new Tab(partner);
                privateTab.isClosable();
                privateTab.setContent(privateChatArea);
                privateTab.setOnSelectionChanged(event -> {
                    if (privateTab.isSelected()) {
                        privateTab.setStyle(null);
                        prefix = "CHAT;PRIVATE;" + privateTab.getText() + ";";
                    }
                });
                privateTab.setOnClosed(event -> {
                    privateChats.remove(privateTab.getText());
                    privateTab.getTabPane().getTabs().remove(privateTab);
                });
                System.out.println(chatTabs.getTabs());
                chatTabs.getTabs().add(privateTab);
                privateChats.put(partner, privateTab);
                chatTabs.getSelectionModel().select(privateTab);

            }
        });
    }

    private void addLineToTextArea(TextArea destTextArea, String msg) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        destTextArea.appendText(timestampFormat.format(timestamp) + " " + msg + "\n");
    }

    /**
     * updates the nickname in a private chat when the partner changes his name
     *
     * @param oldClientName
     * @param newClientName
     */
    void updateNicknames(String oldClientName, String newClientName) {
        Platform.runLater(() -> {
            if (privateChats.containsKey(oldClientName)) {
                privateChats.get(oldClientName).setText(newClientName);
                privateChats.put(newClientName, privateChats.get(oldClientName));
                privateChats.remove(oldClientName);
                ((TextArea) privateChats.get(newClientName).getContent()).appendText(oldClientName + " changed his name to " + newClientName + ".");
            }
        });
    }

    /**
     * updates the scoreboard table which is showing up when clicking the menu item
     *
     * @param scoreboard
     */
    void updateScoreboard(String scoreboard) {
        ObservableList<ScoreEntry> scoreEntries = FXCollections.observableArrayList();
        Platform.runLater(() -> {
            if (!scoreboard.isEmpty()) {
                String[] scores = scoreboard.split("\n");
                int i = 1;
                for (String score : scores) {
                    String[] scoreSplit = score.split("=");
                    scoreEntries.add(new ScoreEntry(i, scoreSplit[0], Integer.parseInt(scoreSplit[1])));
                    i++;
                    if (i == 11) {
                        break;
                    }
                }
                scoreboardTable.setItems(scoreEntries);
            }
        });
    }

    /**
     * creates a tree view of the lobby string given by the server
     *
     * @param lobbyList
     */
    void updateLobbyList(String lobbyList) {
        Platform.runLater(() -> {
            //root creation
            TreeItem<String> root = new TreeItem<>("Root Node");
            root.setExpanded(true);

            String[] lobbiesAsString = lobbyList.split(";");
            for (String lobby : lobbiesAsString) {
                String[] clients = lobby.split(":");
                TreeItem<String> lobbyItem = new TreeItem<>(clients[0]);
                for (int i = 1; i < clients.length; i++) {
                    lobbyItem.getChildren().add(new TreeItem<>(clients[i]));
                }
                lobbyItem.expandedProperty().addListener(observable -> {
                    if (!lobbyItem.isExpanded()) {
                        lobbyItem.setExpanded(true);
                    }
                });
                lobbyItem.setExpanded(true);
                root.getChildren().add(lobbyItem);
            }

            //set tree options and append to tab
            TreeView<String> treeView = new TreeView<>(root);
            treeView.setShowRoot(false);
            treeView.setEditable(false);
            treeView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null && !selectedItem.getChildren().isEmpty()) {
                        if (event.getClickCount() == 2) {
                            GUIClient.enterLobby(selectedItem.getValue());
                            event.consume();
                        }
                    }
                }
            });
            lobbiesTab.setContent(treeView);
        });
    }

    void createErrorAlert(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText(errorMessage);
            alert.initOwner(GUIClient.getLobbyStage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.showAndWait();
        });
    }

    /**
     * closes lobby chat tab and clears text area
     */
    void leaveLobby() {
        lobbyMessageArea.clear();
        chatTabs.getTabs().remove(lobbyChatTab);
        GUIClient.messageServer("LEAVE");
    }

    /**
     * closes old lobby chat tab and open a new one for the specified lobby
     *
     * @param lobbyName the name of the lobby
     */
    void enterLobby(String lobbyName) {
        Platform.runLater(() -> {
            chatTabs.getTabs().remove(lobbyChatTab);
            lobbyChatTab = new Tab(lobbyName);
            lobbyChatTab.setClosable(true);
            lobbyChatTab.setOnSelectionChanged(event -> {
                if (lobbyChatTab.isSelected()) {
                    lobbyChatTab.setStyle(null);
                    prefix = "CHAT;LOBBY;" + lobbyChatTab.getText() + ";";
                }
            });
            lobbyChatTab.setOnCloseRequest(event -> leaveLobby());
            lobbyChatTab.setContent(lobbyAnchorPane);
            chatTabs.getTabs().add(lobbyChatTab);
            chatTabs.getSelectionModel().select(lobbyChatTab);
            toggleReadyButton.setDisable(false);
            toggleReadyButton.setSelected(false);

        });
    }

    void openGameWindow() {
        Platform.runLater(() -> {
            if (gameFullscreen.isSelected()) {
                GUIClient.getGameStage().setFullScreen(true);
            }
            GUIClient.unmuteGame();
            GUIClient.getGameStage().show();
        });
    }

    /**
     * clear all entries for reuse
     */
    void clear() {
        Platform.runLater(() -> {
            serverMessageArea.clear();
            lobbyMessageArea.clear();
            chatTabs.getTabs().removeAll(chatTabs.getTabs());
            chatTabs.getTabs().add(serverChatTab);
        });
    }

    /**
     * a class to easier create entries for the scoreboard table
     */
    public static class ScoreEntry {
        private final SimpleIntegerProperty position;
        private final SimpleStringProperty playerName;
        private final SimpleIntegerProperty timesWon;

        private ScoreEntry(int position, String playerName, int timesWon) {
            this.position = new SimpleIntegerProperty(position);
            this.playerName = new SimpleStringProperty(playerName);
            this.timesWon = new SimpleIntegerProperty(timesWon);
        }

        public int getPosition() {
            return position.get();
        }

        public void setPosition(int pos) {
            position.set(pos);
        }

        public String getPlayerName() {
            return playerName.get();
        }

        public void setPlayerName(String name) {
            playerName.set(name);
        }

        public int getTimesWon() {
            return timesWon.get();
        }

        public void setTimesWon(int x) {
            timesWon.set(x);
        }

    }

}

