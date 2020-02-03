package bdgame.apps.client.GUI;

import bdgame.game.Colors;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

/**
 * This is the controller class for the fxml file
 *
 * @author Gowthaman
 */
public class GameController implements Initializable {

    /**
     * Load all 52 cards
     */
    private final Image no_card = new Image(getClass().getResource("/images/cards/card_nocard.jpg").toExternalForm());
    private final Image TWO_CLUBS = new Image(getClass().getResource("/images/cards/Two_club.jpg").toExternalForm());
    private final Image TWO_DIAMONDS = new Image(getClass().getResource("/images/cards/Two_diamond.jpg").toExternalForm());
    private final Image TWO_HEARTS = new Image(getClass().getResource("/images/cards/Two_heart.jpg").toExternalForm());
    private final Image TWO_SPADES = new Image(getClass().getResource("/images/cards/Two_spade.jpg").toExternalForm());
    private final Image THREE_CLUBS = new Image(getClass().getResource("/images/cards/Three_club.jpg").toExternalForm());
    private final Image THREE_DIAMONDS = new Image(getClass().getResource("/images/cards/Three_diamond.jpg").toExternalForm());
    private final Image THREE_HEARTS = new Image(getClass().getResource("/images/cards/Three_heart.jpg").toExternalForm());
    private final Image THREE_SPADES = new Image(getClass().getResource("/images/cards/Three_spade.jpg").toExternalForm());
    private final Image FOUR_CLUBS = new Image(getClass().getResource("/images/cards/Four_club.jpg").toExternalForm());
    private final Image FOUR_DIAMONDS = new Image(getClass().getResource("/images/cards/Four_diamond.jpg").toExternalForm());
    private final Image FOUR_HEARTS = new Image(getClass().getResource("/images/cards/Four_heart.jpg").toExternalForm());
    private final Image FOUR_SPADES = new Image(getClass().getResource("/images/cards/Four_spade.jpg").toExternalForm());
    private final Image FIVE_CLUBS = new Image(getClass().getResource("/images/cards/Five_club.jpg").toExternalForm());
    private final Image FIVE_DIAMONDS = new Image(getClass().getResource("/images/cards/Five_diamond.jpg").toExternalForm());
    private final Image FIVE_HEARTS = new Image(getClass().getResource("/images/cards/Five_heart.jpg").toExternalForm());
    private final Image FIVE_SPADES = new Image(getClass().getResource("/images/cards/Five_spade.jpg").toExternalForm());
    private final Image SIX_CLUBS = new Image(getClass().getResource("/images/cards/Six_club.jpg").toExternalForm());
    private final Image SIX_DIAMONDS = new Image(getClass().getResource("/images/cards/Six_diamond.jpg").toExternalForm());
    private final Image SIX_HEARTS = new Image(getClass().getResource("/images/cards/Six_heart.jpg").toExternalForm());
    private final Image SIX_SPADES = new Image(getClass().getResource("/images/cards/Six_spade.jpg").toExternalForm());
    private final Image SEVEN_CLUBS = new Image(getClass().getResource("/images/cards/Seven_club.jpg").toExternalForm());
    private final Image SEVEN_DIAMONDS = new Image(getClass().getResource("/images/cards/Seven_diamond.jpg").toExternalForm());
    private final Image SEVEN_HEARTS = new Image(getClass().getResource("/images/cards/Seven_heart.jpg").toExternalForm());
    private final Image SEVEN_SPADES = new Image(getClass().getResource("/images/cards/Seven_spade.jpg").toExternalForm());
    private final Image EIGHT_CLUBS = new Image(getClass().getResource("/images/cards/Eigth_club.jpg").toExternalForm());
    private final Image EIGHT_DIAMONDS = new Image(getClass().getResource("/images/cards/Eigth_diamond.jpg").toExternalForm());
    private final Image EIGHT_HEARTS = new Image(getClass().getResource("/images/cards/Eigth_heart.jpg").toExternalForm());
    private final Image EIGHT_SPADES = new Image(getClass().getResource("/images/cards/Eigth_spade.jpg").toExternalForm());
    private final Image NINE_CLUBS = new Image(getClass().getResource("/images/cards/Nine_club.jpg").toExternalForm());
    private final Image NINE_DIAMONDS = new Image(getClass().getResource("/images/cards/Nine_diamond.jpg").toExternalForm());
    private final Image NINE_HEARTS = new Image(getClass().getResource("/images/cards/Nine_heart.jpg").toExternalForm());
    private final Image NINE_SPADES = new Image(getClass().getResource("/images/cards/Nine_spade.jpg").toExternalForm());
    private final Image TEN_CLUBS = new Image(getClass().getResource("/images/cards/Ten_club.jpg").toExternalForm());
    private final Image TEN_DIAMONDS = new Image(getClass().getResource("/images/cards/Ten_diamond.jpg").toExternalForm());
    private final Image TEN_HEARTS = new Image(getClass().getResource("/images/cards/Ten_heart.jpg").toExternalForm());
    private final Image TEN_SPADES = new Image(getClass().getResource("/images/cards/Ten_spade.jpg").toExternalForm());
    private final Image JACK_CLUBS = new Image(getClass().getResource("/images/cards/Jack_club.jpg").toExternalForm());
    private final Image JACK_DIAMONDS = new Image(getClass().getResource("/images/cards/Jack_diamond.jpg").toExternalForm());
    private final Image JACK_HEARTS = new Image(getClass().getResource("/images/cards/Jack_heart.jpg").toExternalForm());
    private final Image JACK_SPADES = new Image(getClass().getResource("/images/cards/Jack_spade.jpg").toExternalForm());
    private final Image QUEEN_CLUBS = new Image(getClass().getResource("/images/cards/Queen_club.jpg").toExternalForm());
    private final Image QUEEN_DIAMONDS = new Image(getClass().getResource("/images/cards/Queen_diamond.jpg").toExternalForm());
    private final Image QUEEN_HEARTS = new Image(getClass().getResource("/images/cards/Queen_heart.jpg").toExternalForm());
    private final Image QUEEN_SPADES = new Image(getClass().getResource("/images/cards/Queen_spade.jpg").toExternalForm());
    private final Image KING_CLUBS = new Image(getClass().getResource("/images/cards/King_club.jpg").toExternalForm());
    private final Image KING_DIAMONDS = new Image(getClass().getResource("/images/cards/King_diamond.jpg").toExternalForm());
    private final Image KING_HEARTS = new Image(getClass().getResource("/images/cards/King_heart.jpg").toExternalForm());
    private final Image KING_SPADES = new Image(getClass().getResource("/images/cards/King_spade.jpg").toExternalForm());
    private final Image ACE_CLUBS = new Image(getClass().getResource("/images/cards/Ace_club.jpg").toExternalForm());
    private final Image ACE_DIAMONDS = new Image(getClass().getResource("/images/cards/Ace_diamond.jpg").toExternalForm());
    private final Image ACE_HEARTS = new Image(getClass().getResource("/images/cards/Ace_heart.jpg").toExternalForm());
    private final Image ACE_SPADES = new Image(getClass().getResource("/images/cards/Ace_spade.jpg").toExternalForm());
    @FXML
    private Font x1;
    @FXML
    private Text errorText;
    @FXML
    private Text infoText;
    @FXML
    private Label winLabel;
    /**
     * All balls declared as ImageViews
     */
    @FXML
    private ImageView RED1;
    @FXML
    private ImageView RED2;
    @FXML
    private ImageView RED3;
    @FXML
    private ImageView RED4;
    @FXML
    private ImageView BLUE1;
    @FXML
    private ImageView BLUE2;
    @FXML
    private ImageView BLUE3;
    @FXML
    private ImageView BLUE4;
    @FXML
    private ImageView GREEN1;
    @FXML
    private ImageView GREEN2;
    @FXML
    private ImageView GREEN3;
    @FXML
    private ImageView GREEN4;
    @FXML
    private ImageView YELLOW1;
    @FXML
    private ImageView YELLOW2;
    @FXML
    private ImageView YELLOW3;
    @FXML
    private ImageView YELLOW4;
    /**
     * All 64 fields declared as Pane
     */
    @FXML
    private Pane field1;
    @FXML
    private Pane field2;
    @FXML
    private Pane field3;
    @FXML
    private Pane field4;
    @FXML
    private Pane field5;
    @FXML
    private Pane field6;
    @FXML
    private Pane field7;
    @FXML
    private Pane field8;
    @FXML
    private Pane field9;
    @FXML
    private Pane field10;
    @FXML
    private Pane field11;
    @FXML
    private Pane field12;
    @FXML
    private Pane field13;
    @FXML
    private Pane field14;
    @FXML
    private Pane field15;
    @FXML
    private Pane field16;
    @FXML
    private Pane field17;
    @FXML
    private Pane field18;
    @FXML
    private Pane field19;
    @FXML
    private Pane field20;
    @FXML
    private Pane field21;
    @FXML
    private Pane field22;
    @FXML
    private Pane field23;
    @FXML
    private Pane field24;
    @FXML
    private Pane field25;
    @FXML
    private Pane field26;
    @FXML
    private Pane field27;
    @FXML
    private Pane field28;
    @FXML
    private Pane field29;
    @FXML
    private Pane field30;
    @FXML
    private Pane field31;
    @FXML
    private Pane field32;
    @FXML
    private Pane field33;
    @FXML
    private Pane field34;
    @FXML
    private Pane field35;
    @FXML
    private Pane field36;
    @FXML
    private Pane field37;
    @FXML
    private Pane field38;
    @FXML
    private Pane field39;
    @FXML
    private Pane field40;
    @FXML
    private Pane field41;
    @FXML
    private Pane field42;
    @FXML
    private Pane field43;
    @FXML
    private Pane field44;
    @FXML
    private Pane field45;
    @FXML
    private Pane field46;
    @FXML
    private Pane field47;
    @FXML
    private Pane field48;
    @FXML
    private Pane field49;
    @FXML
    private Pane field50;
    @FXML
    private Pane field51;
    @FXML
    private Pane field52;
    @FXML
    private Pane field53;
    @FXML
    private Pane field54;
    @FXML
    private Pane field55;
    @FXML
    private Pane field56;
    @FXML
    private Pane field57;
    @FXML
    private Pane field58;
    @FXML
    private Pane field59;
    @FXML
    private Pane field60;
    @FXML
    private Pane field61;
    @FXML
    private Pane field62;
    @FXML
    private Pane field63;
    @FXML
    private Pane field64;
    /**
     * All pit declared as Pane
     */
    @FXML
    private Pane BLUEPit1;
    @FXML
    private Pane BLUEPit2;
    @FXML
    private Pane BLUEPit3;
    @FXML
    private Pane BLUEPit4;
    @FXML
    private Pane GREENPit1;
    @FXML
    private Pane GREENPit2;
    @FXML
    private Pane GREENPit3;
    @FXML
    private Pane GREENPit4;
    @FXML
    private Pane REDPit1;
    @FXML
    private Pane REDPit2;
    @FXML
    private Pane REDPit3;
    @FXML
    private Pane REDPit4;
    @FXML
    private Pane YELLOWPit1;
    @FXML
    private Pane YELLOWPit2;
    @FXML
    private Pane YELLOWPit3;
    @FXML
    private Pane YELLOWPit4;
    /**
     * Homefields declared as panes
     */

    @FXML
    private Pane blueHome1;
    @FXML
    private Pane blueHome2;
    @FXML
    private Pane blueHome3;
    @FXML
    private Pane blueHome4;
    @FXML
    private Pane redHome1;
    @FXML
    private Pane redHome2;
    @FXML
    private Pane redHome3;
    @FXML
    private Pane redHome4;
    @FXML
    private Pane greenHome1;
    @FXML
    private Pane greenHome2;
    @FXML
    private Pane greenHome3;
    @FXML
    private Pane greenHome4;
    @FXML
    private Pane yellowHome1;
    @FXML
    private Pane yellowHome2;
    @FXML
    private Pane yellowHome3;
    @FXML
    private Pane yellowHome4;
    /**
     * Cards, which could be choose to play are showed as buttons
     */
    @FXML
    private GridPane cardGridPane;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private Button button5;
    @FXML
    private Button button6;
    //played card stack
    @FXML
    private ImageView playedCards;
    /*
    * Turn related items
    * */
    @FXML
    private ImageView whichColorBall;
    @FXML
    private Label whichColorLabel;
    @FXML
    private ImageView turnBall;
    @FXML
    private Label turnLabel;
    //window moving
    private double xOffset = 0;
    private double yOffset = 0;
    //Game related variables
    private Colors.PlayerColors ownColor;
    private String cardToPlay;
    private String tokenToPlay;
    private boolean gameRunning;
    /**
     * Card HashMaps
     */

    private HashMap<String, Image> allCardsByName;
    private HashMap<Image, String> allCardsByImage;
    /**
     * All ImageView balls saved in a hashmap, which be used in gamelogic
     */
    private HashMap<ImageView, Boolean> tokens;
    /**
     * All Pane fields saved in a array, which be used in gamelogic
     */
    private HashMap<Integer, Pane> fields;
    /**
     * All pits as Panes saved in hashMaps, which be used in gamelogic
     */
    private HashMap<Integer, Pane> redPits;
    private HashMap<Integer, Pane> bluePits;
    private HashMap<Integer, Pane> yellowPits;
    private HashMap<Integer, Pane> greenPits;
    private HashMap<Integer, Pane> redHomes;
    private HashMap<Integer, Pane> blueHomes;
    private HashMap<Integer, Pane> greenHomes;
    private HashMap<Integer, Pane> yellowHomes;
    /**
     * All Buttons saved in arrays, which be used in gamelogc
     */
    private Button[] cardButtons;
    /**
     * *
     * Sounds
     */

    private AudioClip movementSound;
    private AudioClip sendHomeSound;
    private AudioClip loserSound;
    private AudioClip winnerSound;


    @FXML
    void mousePressedOnMenuBar(MouseEvent event) {
        xOffset = GUIClient.getGameStage().getX() - event.getScreenX();
        yOffset = GUIClient.getGameStage().getY() - event.getScreenY();
    }

    @FXML
    void mouseDragsMenuBar(MouseEvent event) {
        GUIClient.getGameStage().setX(event.getScreenX() + xOffset);
        GUIClient.getGameStage().setY(event.getScreenY() + yOffset);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardToPlay = "";
        tokenToPlay = "";
        gameRunning = false;
        initPlayerCards();
        initPits();
        initHomes();
        initTokens();
        initFields();
        initCards();
        turnLabel.setText("");
        winLabel.setText("");
        movementSound = new AudioClip(getClass().getResource("/sounds/move.mp3").toExternalForm());
        loserSound = new AudioClip(getClass().getResource("/sounds/loser.mp3").toExternalForm());
        sendHomeSound = new AudioClip(getClass().getResource("/sounds/remove.mp3").toExternalForm());
        winnerSound = new AudioClip(getClass().getResource("/sounds/winner.mp3").toExternalForm());
        muteSounds();
    }


    void initUI() {
        cardToPlay = "";
        tokenToPlay = "";
        gameRunning = false;
        winLabel.setText("");
    }

    private void initCards() {
        Platform.runLater(() -> {
            allCardsByName = new HashMap<>();
            allCardsByImage = new HashMap<>();

            //clubs
            allCardsByName.put("TWO:CLUBS", TWO_CLUBS);
            allCardsByName.put("THREE:CLUBS", THREE_CLUBS);
            allCardsByName.put("FOUR:CLUBS", FOUR_CLUBS);
            allCardsByName.put("FIVE:CLUBS", FIVE_CLUBS);
            allCardsByName.put("SIX:CLUBS", SIX_CLUBS);
            allCardsByName.put("SEVEN:CLUBS", SEVEN_CLUBS);
            allCardsByName.put("EIGHT:CLUBS", EIGHT_CLUBS);
            allCardsByName.put("NINE:CLUBS", NINE_CLUBS);
            allCardsByName.put("TEN:CLUBS", TEN_CLUBS);
            allCardsByName.put("JACK:CLUBS", JACK_CLUBS);
            allCardsByName.put("QUEEN:CLUBS", QUEEN_CLUBS);
            allCardsByName.put("KING:CLUBS", KING_CLUBS);
            allCardsByName.put("ACE:CLUBS", ACE_CLUBS);

            //hearts
            allCardsByName.put("TWO:HEARTS", TWO_HEARTS);
            allCardsByName.put("THREE:HEARTS", THREE_HEARTS);
            allCardsByName.put("FOUR:HEARTS", FOUR_HEARTS);
            allCardsByName.put("FIVE:HEARTS", FIVE_HEARTS);
            allCardsByName.put("SIX:HEARTS", SIX_HEARTS);
            allCardsByName.put("SEVEN:HEARTS", SEVEN_HEARTS);
            allCardsByName.put("EIGHT:HEARTS", EIGHT_HEARTS);
            allCardsByName.put("NINE:HEARTS", NINE_HEARTS);
            allCardsByName.put("TEN:HEARTS", TEN_HEARTS);
            allCardsByName.put("JACK:HEARTS", JACK_HEARTS);
            allCardsByName.put("QUEEN:HEARTS", QUEEN_HEARTS);
            allCardsByName.put("KING:HEARTS", KING_HEARTS);
            allCardsByName.put("ACE:HEARTS", ACE_HEARTS);

            //diamonds
            allCardsByName.put("TWO:DIAMONDS", TWO_DIAMONDS);
            allCardsByName.put("THREE:DIAMONDS", THREE_DIAMONDS);
            allCardsByName.put("FOUR:DIAMONDS", FOUR_DIAMONDS);
            allCardsByName.put("FIVE:DIAMONDS", FIVE_DIAMONDS);
            allCardsByName.put("SIX:DIAMONDS", SIX_DIAMONDS);
            allCardsByName.put("SEVEN:DIAMONDS", SEVEN_DIAMONDS);
            allCardsByName.put("EIGHT:DIAMONDS", EIGHT_DIAMONDS);
            allCardsByName.put("NINE:DIAMONDS", NINE_DIAMONDS);
            allCardsByName.put("TEN:DIAMONDS", TEN_DIAMONDS);
            allCardsByName.put("JACK:DIAMONDS", JACK_DIAMONDS);
            allCardsByName.put("QUEEN:DIAMONDS", QUEEN_DIAMONDS);
            allCardsByName.put("KING:DIAMONDS", KING_DIAMONDS);
            allCardsByName.put("ACE:DIAMONDS", ACE_DIAMONDS);

            //spades
            allCardsByName.put("TWO:SPADES", TWO_SPADES);
            allCardsByName.put("THREE:SPADES", THREE_SPADES);
            allCardsByName.put("FOUR:SPADES", FOUR_SPADES);
            allCardsByName.put("FIVE:SPADES", FIVE_SPADES);
            allCardsByName.put("SIX:SPADES", SIX_SPADES);
            allCardsByName.put("SEVEN:SPADES", SEVEN_SPADES);
            allCardsByName.put("EIGHT:SPADES", EIGHT_SPADES);
            allCardsByName.put("NINE:SPADES", NINE_SPADES);
            allCardsByName.put("TEN:SPADES", TEN_SPADES);
            allCardsByName.put("JACK:SPADES", JACK_SPADES);
            allCardsByName.put("QUEEN:SPADES", QUEEN_SPADES);
            allCardsByName.put("KING:SPADES", KING_SPADES);
            allCardsByName.put("ACE:SPADES", ACE_SPADES);

            for (Map.Entry<String, Image> entry : allCardsByName.entrySet()) {
                String name = entry.getKey();
                Image image = entry.getValue();
                allCardsByImage.put(image, name);
            }
        });


    }

    private void initHomes() {
        Platform.runLater(() -> {
            redHomes = new HashMap<>();
            redHomes.put(1, redHome1);
            redHomes.put(2, redHome2);
            redHomes.put(3, redHome3);
            redHomes.put(4, redHome4);

            blueHomes = new HashMap<>();
            blueHomes.put(1, blueHome1);
            blueHomes.put(2, blueHome2);
            blueHomes.put(3, blueHome3);
            blueHomes.put(4, blueHome4);

            yellowHomes = new HashMap<>();
            yellowHomes.put(1, yellowHome1);
            yellowHomes.put(2, yellowHome2);
            yellowHomes.put(3, yellowHome3);
            yellowHomes.put(4, yellowHome4);

            greenHomes = new HashMap<>();
            greenHomes.put(1, greenHome1);
            greenHomes.put(2, greenHome2);
            greenHomes.put(3, greenHome3);
            greenHomes.put(4, greenHome4);
        });
    }

    private void initPits() {
        Platform.runLater(() -> {
            redPits = new HashMap<>();
            redPits.put(1, REDPit1);
            redPits.put(2, REDPit2);
            redPits.put(3, REDPit3);
            redPits.put(4, REDPit4);

            bluePits = new HashMap<>();
            bluePits.put(1, BLUEPit1);
            bluePits.put(2, BLUEPit2);
            bluePits.put(3, BLUEPit3);
            bluePits.put(4, BLUEPit4);

            yellowPits = new HashMap<>();
            yellowPits.put(1, YELLOWPit1);
            yellowPits.put(2, YELLOWPit2);
            yellowPits.put(3, YELLOWPit3);
            yellowPits.put(4, YELLOWPit4);

            greenPits = new HashMap<>();
            greenPits.put(1, GREENPit1);
            greenPits.put(2, GREENPit2);
            greenPits.put(3, GREENPit3);
            greenPits.put(4, GREENPit4);

        });


    }

    private void initFields() {
        Platform.runLater(() -> {
            Pane[] fieldArray = new Pane[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10,
                    field11, field12, field13, field14, field15, field16, field17, field18, field19, field20,
                    field21, field22, field23, field24, field25, field26, field27, field28, field29, field30,
                    field31, field32, field33, field34, field35, field36, field37, field38, field39, field40,
                    field41, field42, field43, field44, field45, field46, field47, field48, field49, field50,
                    field51, field52, field53, field54, field55, field56, field57, field58, field59, field60,
                    field61, field62, field63, field64};
            fields = new HashMap<>();
            int i = 1;
            for (Pane field : fieldArray) {
                fields.put(i, field);
                i++;
            }
        });


    }

    private void initTokens() {
        Platform.runLater(() -> {
            ImageView[] balls = {BLUE1, BLUE2, BLUE3, BLUE4,
                    GREEN1, GREEN2, GREEN3, GREEN4,
                    YELLOW1, YELLOW2, YELLOW3, YELLOW4,
                    RED1, RED2, RED3, RED4};
            tokens = new HashMap<>();
            int blue = 1;
            int yellow = 1;
            int green = 1;
            int red = 1;
            for (ImageView token : balls) {
                if (token.getId().startsWith(Colors.PlayerColors.BLUE.name())) {
                    moveToken(token, blueHomes.get(blue));
                    blue++;
                }
                if (token.getId().startsWith(Colors.PlayerColors.YELLOW.name())) {
                    moveToken(token, yellowHomes.get(yellow));
                    yellow++;
                }
                if (token.getId().startsWith(Colors.PlayerColors.RED.name())) {
                    moveToken(token, redHomes.get(red));
                    red++;
                }
                if (token.getId().startsWith(Colors.PlayerColors.GREEN.name())) {
                    moveToken(token, greenHomes.get(green));
                    green++;
                }
                token.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (tokens.get((ImageView) event.getSource()) || cardToPlay.startsWith("JACK")) {
                            if (!cardToPlay.isEmpty()) {
                                String[] cardToPlayValues = cardToPlay.split(":");
                                switch (cardToPlayValues[0]) {
                                    case "JACK":
                                        if (!tokenToPlay.isEmpty()) {
                                            sendActionToServer(((ImageView) event.getSource()).getId());
                                        } else
                                            showInfo("Select marble to switch position with.");
                                        tokenToPlay = ((ImageView) event.getSource()).getId();
                                        break;
                                    case "ACE":
                                        if (((Pane) ((ImageView) event.getSource()).getParent()).getId().contains("Home")) {
                                            tokenToPlay = ((ImageView) event.getSource()).getId();
                                            sendActionToServer("");
                                        } else {
                                            tokenToPlay = ((ImageView) event.getSource()).getId();
                                            openDialogForCardAction();
                                        }
                                        break;
                                    case "SEVEN":
                                        tokenToPlay = ((ImageView) event.getSource()).getId();
                                        openDialogForCardAction();
                                        break;
                                    case "FOUR":
                                        tokenToPlay = ((ImageView) event.getSource()).getId();
                                        openDialogForCardAction();
                                        break;
                                    default:
                                        tokenToPlay = ((ImageView) event.getSource()).getId();
                                        sendActionToServer("");
                                }
                            } else {
                                showError("No card chosen.");
                            }
                        }
                    }
                });
                tokens.put(token, false);
            }
        });

    }

    /**
     * shows the win state of the game
     *
     * @param winner winners name
     * @param color  winners color
     */
    void setGameEnd(String winner, String color) {
        Platform.runLater(() -> {
            gameRunning = false;
            turnLabel.setText("");
            turnBall.setVisible(false);
            winLabel.setText("Game Over!\n" + winner + " won!");
            for (Button button : cardButtons) {
                button.setDisable(true);
            }
            if (color.equals(ownColor.name())) {
                winnerSound.play();
            } else {
                loserSound.play();
            }
        });

    }

    private void sendActionToServer(String specialSuffix) {
        if (!(tokenToPlay.isEmpty() && cardToPlay.isEmpty())) {
            GUIClient.messageServer("GAME;PLAY;" + ownColor.name() + ";" + cardToPlay + ";" + tokenToPlay + ";" + specialSuffix);
            tokenToPlay = "";
            cardToPlay = "";
        }
    }


    /**
     * opens a dialog for special card to choose one of the available actions
     * if cancelled the turn starts over
     */
    private void openDialogForCardAction() {
        ImageView imageView = new ImageView(allCardsByName.get(cardToPlay));
        imageView.setFitHeight(143);
        imageView.setFitWidth(93);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(null);
        alert.setGraphic(imageView);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.initOwner(GUIClient.getGameStage());
        alert.initModality(Modality.WINDOW_MODAL);

        ArrayList<ButtonType> buttons = new ArrayList<>();
        ButtonType buttonTypeCancel = new ButtonType("Cancel");

        switch (cardToPlay.split(":")[0]) {
            case "ACE":
                alert.setContentText("Choose how many steps:");
                buttons.add(new ButtonType("1"));
                buttons.add(new ButtonType("11"));
                break;
            case "SEVEN":
                alert.setContentText("Choose direction:");
                buttons.add(new ButtonType("forward"));
                buttons.add(new ButtonType("backward"));
                break;
            case "FOUR":
                alert.setContentText("Choose how far:");
                buttons.add(new ButtonType("1"));
                buttons.add(new ButtonType("2"));
                buttons.add(new ButtonType("3"));
                buttons.add(new ButtonType("4"));
                break;
        }

        buttons.add(buttonTypeCancel);
        alert.getButtonTypes().setAll(buttons);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get().getText().equals("Cancel")) {
                cardToPlay = "";
                tokenToPlay = "";
            } else {
                sendActionToServer(result.get().getText());
            }
        }
    }

    void muteSounds() {
        loserSound.setVolume(0.0);
        winnerSound.setVolume(0.0);
        movementSound.setVolume(0.0);
        sendHomeSound.setVolume(0.0);
    }

    void unmuteSounds() {
        loserSound.setVolume(1.0);
        winnerSound.setVolume(1.0);
        movementSound.setVolume(1.0);
        sendHomeSound.setVolume(1.0);
    }

    void showError(String error) {
        cardToPlay = "";
        tokenToPlay = "";
        Platform.runLater(() -> {
            errorText.setText(error);
            FadeTransition ft = new FadeTransition(Duration.millis(3000), errorText);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);

            ft.play();
        });
    }

    void showInfo(String info) {
        Platform.runLater(() -> {
            infoText.setText(info);
            FadeTransition ft = new FadeTransition(Duration.millis(3000), infoText);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);

            ft.play();
        });
    }

    void setTurn(String playerName, String color) {
        Platform.runLater(() -> {
            turnLabel.setText("It's " + playerName + "s turn.");
            switch (Colors.PlayerColors.valueOf(color)) {
                case BLUE:
                    turnBall.setImage(BLUE1.getImage());
                    break;
                case GREEN:
                    turnBall.setImage(GREEN1.getImage());
                    break;
                case YELLOW:
                    turnBall.setImage(YELLOW1.getImage());
                    break;
                case RED:
                    turnBall.setImage(RED1.getImage());
                    break;
                default:
                    createErrorAlert("Error setting TurnBall: " + color);
                    break;
            }
            for (Button button : cardButtons) {
                if (((ImageView) button.getGraphic()).getImage() != no_card)
                    button.setDisable(!(Colors.PlayerColors.valueOf(color) == ownColor));
            }
            cardToPlay = "";
            tokenToPlay = "";
            gameRunning = true;

        });
    }

    private void initPlayerCards() {
        Platform.runLater(() -> {
            cardButtons = new Button[]{button1, button2, button3, button4, button5, button6};
            for (Button button : cardButtons) {
                button.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (!((Button) event.getSource()).isDisabled()) {
                            playCard((Button) event.getSource());
                        }
                    }

                });
                assignCard(button, no_card);
            }
        });

    }

    void clearHand() {
        Platform.runLater(() -> {
            for (Button button : cardButtons) {
                ImageView imageView = new ImageView(no_card);
                int cardHeight = 215;
                imageView.setFitHeight(cardHeight);
                int cardWidth = 140;
                imageView.setFitWidth(cardWidth);
                button.setGraphic(imageView);
                button.setDisable(true);
            }
        });
    }

    void setOwnColor(String color) {
        Platform.runLater(() -> {
            switch (Colors.PlayerColors.valueOf(color)) {
                case RED:
                    whichColorBall.setImage(RED1.getImage());
                    break;
                case BLUE:
                    whichColorBall.setImage(BLUE1.getImage());
                    break;
                case GREEN:
                    whichColorBall.setImage(GREEN1.getImage());
                    break;
                case YELLOW:
                    whichColorBall.setImage(YELLOW1.getImage());
                    break;
            }
            ownColor = Colors.PlayerColors.valueOf(color);
            setTokenPlayable(ownColor.name(), true);
        });

    }

    private void setTokenPlayable(String color, boolean playable) {
        Platform.runLater(() -> {
            for (ImageView token : tokens.keySet()) {
                if (token.getId().startsWith(color)) {
                    tokens.put(token, playable);
                }
            }
        });

    }

    private void playCard(Button button) {
        Platform.runLater(() -> cardToPlay = allCardsByImage.get(((ImageView) button.getGraphic()).getImage()));

    }

    void setLastPlayedCard(String cardName) {
        Platform.runLater(() -> playedCards.setImage(allCardsByName.get(cardName)));
    }

    /**
     * moving the token and playing a sound (one for normal field movement, one for
     * sending a token home)
     *
     * @param token
     * @param destinationField
     */
    private void moveToken(ImageView token, Pane destinationField) {
        Platform.runLater(() -> {
            destinationField.getChildren().clear();
            destinationField.getChildren().add(token);
            token.relocate(destinationField.getTranslateX(), destinationField.getTranslateY());
            if (destinationField.getId().contains("Home"))
                sendHomeSound.play();
            else
                movementSound.play();
        });
    }

    /**
     * sets the values to move the token specified to the pane specified by the destinationfield
     *
     * @param tokenName        the token to be moved
     * @param destinationField the target field
     */
    void setTokenPosition(String tokenName, String destinationField) {
        Platform.runLater(() -> {
            Pane destinationPane = new Pane();
            ImageView token = BLUE1;
            if (destinationField.length() < 3) {
                if (fields.containsKey(Integer.parseInt(destinationField))) {
                    destinationPane = fields.get(Integer.parseInt(destinationField));
                }
            } else {
                String numberString = destinationField.replaceAll("PIT", "");
                numberString = numberString.replaceAll("HOME", "");
                int number = Integer.parseInt(numberString.substring(numberString.length() - 1));
                if (destinationField.contains("PIT")) {
                    if (destinationField.startsWith(Colors.PlayerColors.RED.name())) {
                        destinationPane = redPits.get(number);
                    }
                    if (destinationField.startsWith(Colors.PlayerColors.BLUE.name())) {
                        destinationPane = bluePits.get(number);
                    }
                    if (destinationField.startsWith(Colors.PlayerColors.GREEN.name())) {
                        destinationPane = greenPits.get(number);
                    }
                    if (destinationField.startsWith(Colors.PlayerColors.YELLOW.name())) {
                        destinationPane = yellowPits.get(number);
                    }
                } else {
                    if (destinationField.contains("HOME")) {
                        if (destinationField.startsWith(Colors.PlayerColors.RED.name())) {
                            destinationPane = redHomes.get(number);
                        }
                        if (destinationField.startsWith(Colors.PlayerColors.BLUE.name())) {
                            destinationPane = blueHomes.get(number);
                        }
                        if (destinationField.startsWith(Colors.PlayerColors.GREEN.name())) {
                            destinationPane = greenHomes.get(number);
                        }
                        if (destinationField.startsWith(Colors.PlayerColors.YELLOW.name())) {
                            destinationPane = yellowHomes.get(number);
                        }
                    }
                }
            }

            for (ImageView theTokens : tokens.keySet()) {
                if (theTokens.getId().equals(tokenName)) {
                    token = theTokens;
                    break;
                }
            }
            moveToken(token, destinationPane);
        });

    }


    private void assignCard(Button button, Image card) {
        Platform.runLater(() -> {
            ImageView imageView = new ImageView(card);
            int cardHeight = 215;
            imageView.setFitHeight(cardHeight);
            int cardWidth = 140;
            imageView.setFitWidth(cardWidth);
            button.setGraphic(imageView);
            if (card == no_card) {
                button.setDisable(true);
            } else {
                button.setDisable(false);
            }
        });
    }

    /**
     * adds a card to a free slot in hand
     *
     * @param card
     */
    void addCardToHand(String card) {
        Platform.runLater(() -> {
            Image cardAdded = allCardsByName.get(card);
            for (Button button : cardButtons) {
                //immediate change (can't use assignCard() here)
                ImageView imageView = new ImageView(cardAdded);
                int cardHeight = 215;
                imageView.setFitHeight(cardHeight);
                int cardWidth = 140;
                imageView.setFitWidth(cardWidth);
                if (((ImageView) button.getGraphic()).getImage() == no_card) {
                    button.setGraphic(imageView);
                    break;
                }

            }
        });
    }

    /**
     * changes button to the no card texture
     *
     * @param card
     */
    void removeCardFromHand(String card) {
        Platform.runLater(() -> {
            Image cardAdded = allCardsByName.get(card);
            for (Button button : cardButtons) {
                if (((ImageView) button.getGraphic()).getImage() == cardAdded) {
                    assignCard(button, no_card);
                    break;
                }
            }
        });
    }

    private void createErrorAlert(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Game Error");
            alert.setContentText(errorMessage);
            alert.initOwner(GUIClient.getLobbyStage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.showAndWait();
        });
    }

    void setGameRunning() {
        gameRunning = true;
    }

    /**
     * opens a webview to show a manual in a new window
     */
    @FXML
    private void openManual() {
        Platform.runLater(() -> {
            //GUIClient.getHostServicesFromMainApplication().showDocument(getClass().getResource("/documents/manual.pdf").toExternalForm());

            Stage webViewStage = new Stage();
            Scene scene = new Scene(new Group());

            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.setOnError(event -> System.out.println(event.getMessage()));
            webEngine.setOnAlert(event -> System.out.println(event.getData()));
            String url = getClass().getResource("/documents/manual.html").toExternalForm();
            webEngine.load(url);
            ((Group) scene.getRoot()).getChildren().addAll(webView);
            webViewStage.setScene(scene);
            webViewStage.show();
        });
    }

    /**
     * By clicking the close button in the menubar, the whole game will be closed.
     * If the game is still in a running state it will ask for confirmation and act accordingly
     */
    @FXML
    void exitGame(ActionEvent event) {
        Platform.runLater(() -> {
            if (gameRunning) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Exit Game?");
                alert.setContentText("Do you want to surrender?");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.initOwner(GUIClient.getGameStage());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    GUIClient.closedGame(true);
                    GUIClient.messageServer("GAME;EXIT;" + ownColor);


                }
            } else {
                GUIClient.closedGame(false);
                GUIClient.messageServer("GAME;EXIT");
            }
        });
    }

    @FXML
    private void getPlayedCards(MouseEvent mouseEvent) {
    }
}
