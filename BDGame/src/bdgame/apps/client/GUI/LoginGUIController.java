package bdgame.apps.client.GUI;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginGUIController implements Initializable {
    //non FXML fields
    private String[] loginInfos;
    private boolean loggedIn;


    @FXML
    private TextField serverPortField;

    @FXML
    private Button loginButton;

    @FXML
    private TextField serverAddressField;

    @FXML
    private TextField nicknameField;

    @FXML
    private Button exitButton;

    @FXML
    private Label defaultNameField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loggedIn = false;
        loginInfos = new String[3];
        defaultNameField.setText(defaultNameField.getText() + System.getProperty("user.name"));
        nicknameField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                clickedLogin(new ActionEvent());
            }
        });
        loginInfos[2] = System.getProperty("user.name");
    }

    @FXML
    void clickedLogin(ActionEvent event) {
        loginInfos[0] = serverAddressField.getText().trim();
        loginInfos[1] = serverPortField.getText().trim();
        if (!nicknameField.getText().trim().isEmpty())
            loginInfos[2] = nicknameField.getText().trim();
        else {
            loginInfos[2] = System.getProperty("user.name");
        }
        String status = GUIClient.setUpConnection(loginInfos);
        if (status.equals("success")) {
            status = GUIClient.loginOnServer(loginInfos[2]);
            if (status != null) {
                if (status.equals("success")) {
                    try {
                        loggedIn = true;
                        closeLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    createErrorAlert(status);
                }
            } else
                createErrorAlert("OH NO, SOMETHING BAD HAPPENED :(");

        } else {
            createErrorAlert(status);
        }
    }

    @FXML
    void clickedExit(ActionEvent event) {
        closeLogin();
    }

    private void closeLogin() {
        Stage login = GUIClient.getLoginStage();
        if (loggedIn) {
            login.hide();
            GUIClient.getLobbyStage().show();
            FadeTransition lobbyFadeIn = new FadeTransition(Duration.millis(1500), GUIClient.getLobbyStage().getScene().getRoot());
            lobbyFadeIn.setFromValue(0.0);
            lobbyFadeIn.setToValue(1.0);
            lobbyFadeIn.play();
        } else
            login.close();
    }

    private void createErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login Error");
        alert.setContentText(errorMessage);
        alert.initOwner(GUIClient.getLoginStage());
        alert.initStyle(StageStyle.UNDECORATED);
        alert.showAndWait();
    }

    private void setFocusAndCursor() {
        Platform.runLater(() -> {
            if (!serverPortField.getText().isEmpty() && !serverAddressField.getText().isEmpty()) {
                nicknameField.requestFocus();
                nicknameField.positionCaret(nicknameField.getText().length());
            }
        });
    }

    void setServerAddressField(String serverAddress) {
        if (serverAddress != null && !serverAddress.isEmpty())
            serverAddressField.setText(serverAddress);
    }

    void setServerPortField(String serverPort) {
        if (serverPort != null && !serverPort.isEmpty())
            serverPortField.setText(serverPort);
    }

    void setNicknameField(String nickname) {
        nicknameField.setText(nickname);
        setFocusAndCursor();
    }


}
