package bdgame.apps.server;

import bdgame.game.*;

import java.util.*;

/**
 * The GameThread controls the game logic and the game flow
 */
class GameThread extends Thread {

    private String winner;
    private Deck gameDeck;
    private String activePlayer;
    private HashSet<String> playersCantPlay;
    private HashSet<String> playersSurrender;

    private HashMap<String, ClientThread> clientThreadByName; //Name, ClientThread
    private HashMap<String, String> playerNameByColor; //Color, Name
    private HashMap<String, Hand> handByColor; //Color, Hand
    private ArrayList<String> turnOrder; // Color, OrderNumber
    private String nextAction;
    private Highscore highscoreList;
    private Board board;

    /**
     * Constructor initiliazes all needed data structures and sets colors for the players
     *
     * @param clientMap     the map holding the players that are playing
     * @param highscoreList the highscorelist which is to be updated after the game is done
     */
    GameThread(HashMap<String, ClientThread> clientMap, Highscore highscoreList) {
        clientThreadByName = clientMap;
        playerNameByColor = new HashMap<>();
        handByColor = new HashMap<>();
        turnOrder = new ArrayList<>();
        gameDeck = new Deck();
        board = new Board();
        playersCantPlay = new HashSet<>();
        playersSurrender = new HashSet<>();
        winner = null;

        this.highscoreList = highscoreList;

        Iterator clientIterator = clientThreadByName.entrySet().iterator();
        for (Colors.PlayerColors color : Colors.PlayerColors.values()) {
            if (clientIterator.hasNext()) {
                Map.Entry entry = (Map.Entry) clientIterator.next();
                String player = (String) entry.getKey();
                clientThreadByName.get(player).receiveMessage("GAME;START;" + color);
                playerNameByColor.put(color.toString(), player);
                handByColor.put(color.toString(), new Hand());
                turnOrder.add(color.toString());
            }
        }
        this.start();
    }


    /**
     * The run method controls whose turn it is and how many cards are dealt this round
     * checking for people that surrendered quiete frequently
     */
    public void run() {
        int cardCount = 6;
        while (winner == null) {
            //Round start
            checkForSurrender();
            if (winner != null)
                break;
            for (String turnColor : turnOrder) {
                Hand playersHand = handByColor.get(turnColor);
                emptyPlayersHand(turnColor);
                for (int k = 0; k < cardCount; k++) {
                    Card getCard = gameDeck.dealCard();
                    playersHand.addCard(getCard);
                    messagePlayerByColor(turnColor, "GET;" + getCard.toString());
                }
            }
            while (playersCantPlay.size() < turnOrder.size() && winner == null) {
                checkForSurrender();
                if (winner != null)
                    break;
                for (String turnColor : turnOrder) {
                    activePlayer = turnColor;
                    if (!isAbleToPlay(activePlayer)) {
                        Hand playersHand = handByColor.get(activePlayer);
                        int amountOfCards = playersHand.getSize();
                        if (amountOfCards == 0) {
                            playersCantPlay.add(activePlayer);
                        } else {
                            sendPlayError(activePlayer, "Not able to play. Received new hand.");
                            emptyPlayersHand(activePlayer);
                            for (int k = 0; k < amountOfCards; k++) {
                                Card getCard = gameDeck.dealCard();
                                playersHand.addCard(getCard);
                                messagePlayerByColor(activePlayer, "GET;" + getCard.toString());
                            }
                            playersCantPlay.add(activePlayer);
                        }
                        continue;
                    }
                    if (playersCantPlay.contains(activePlayer)) {
                        playersCantPlay.remove(activePlayer);
                    }
                    messageAllPlayers("SET;TURN;" + activePlayer + ";" + playerNameByColor.get(activePlayer));
                    //wait for players action
                    boolean actionProcessed = false;
                    checkForSurrender();
                    if (winner != null)
                        break;
                    // wait for players action
                    while (!actionProcessed) {
                        synchronized (this) {
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (turnOrder.size() == 1)
                            break;
                        actionProcessed = processAction();
                    }
                }
            }
            checkForSurrender();
            //round end
            playersCantPlay.clear();
            cardCount--;
            gameDeck = new Deck();
            if (cardCount == 1) {
                cardCount = 6;
            }
        }
        messageAllPlayers("FINISH;" + playerNameByColor.get(winner) + ";" + winner);
        updateScoreboard();
    }

    /**
     * prcoesses the action set by retrievePlayerAction
     *
     * @return if the action was processed correctly
     */
    private boolean processAction() {
        String[] action = nextAction.split(";");
        if (action[1].equals("SURRENDER")) {
            return true;
        }
        if (isValidMoveThenExecute()) {
            discardCard(handByColor.get(action[0]).getCardByString(action[1]));
            handByColor.get(action[0]).removeCard(new Card(action[1]));
            messagePlayerByColor(action[0], "SET;CARD;REMOVE;" + action[1]);
            messageAllPlayers("SET;CARD;" + action[1]);
            if (winner == null) {
                if ((winner = board.getWinner()) != null) {
                    playersCantPlay.addAll(turnOrder);
                }
            }
            updateTokenPositions(board.getBoardUpdates());
            return true;
        } else {
            return false;
        }
    }

    /**
     * checking the game rules for the card the player wants to play and the token he selected
     *
     * @return if the players action is possible
     */
    private boolean isValidMoveThenExecute() {
        String[] action = nextAction.split(";");
        String color = action[0];
        String token = action[2];
        String tokenPosition = board.getTokenPosition(token);
        int cardValue = 0;
        switch (action[1].split(":")[0]) {
            case "FOUR":
                cardValue = Integer.parseInt(action[3]);
                break;
            case "SEVEN":
                if (action[3].equals("forward")) {
                    cardValue = 7;
                } else {
                    cardValue = -7;
                }
                break;
            case "JACK":
                String token2 = action[3];
                if (tokenPosition.contains("HOME") || board.getTokenPosition(token2).contains("HOME")) {
                    sendPlayError(color, "Not allowed for tokens in home.");
                    return false;
                } else if (tokenPosition.contains("PIT") || board.getTokenPosition(token2).contains("PIT")) {
                    sendPlayError(color, "Not allowed for tokens in pit.");
                    return false;
                } else {
                    if (!token.startsWith(color) && !token2.startsWith(color)) {
                        sendPlayError(color, "You can't just switch enemy tokens.");
                        return false;
                    }
                    if (token.equals(token2)) {
                        sendPlayError(color, "Dont waste your Jack on 'switching' the same token.");
                        return false;
                    }
                    board.switchTokens(token, token2);
                    return true;
                }
            case "KING":
                if (tokenPosition.contains("HOME")) {
                    if (board.isStartingFieldEmpty(color)) {
                        board.sendTokenHome("" + board.getStartingField(color));
                        board.setTokenToStart(token);
                        return true;
                    } else {
                        sendPlayError(color, "Clear starting field first.");
                        return false;
                    }
                } else {
                    cardValue = 13;
                }
                break;
            case "ACE":
                if (tokenPosition.contains("HOME")) {
                    if (action.length == 3 && board.isStartingFieldEmpty(color)) {
                        board.sendTokenHome("" + board.getStartingField(color));
                        board.setTokenToStart(token);
                        return true;
                    } else {
                        sendPlayError(color, "Clear starting field first.");
                        return false;
                    }
                } else {
                    cardValue = Integer.parseInt(action[3]);
                }
                break;
            case "TWO":
                cardValue = 2;
                break;
            case "THREE":
                cardValue = 3;
                break;
            case "FIVE":
                cardValue = 5;
                break;
            case "SIX":
                cardValue = 6;
                break;
            case "EIGHT":
                cardValue = 8;
                break;
            case "NINE":
                cardValue = 9;
                break;
            case "TEN":
                cardValue = 10;
                break;
            case "QUEEN":
                cardValue = 12;
                break;
        }
        if (tokenPosition.contains("HOME")) {
            sendPlayError(color, "Not a starter card.");
            return false;
        } else {
            if (tokenPosition.contains("PIT")) {
                String stringNumber = tokenPosition.substring(tokenPosition.length() - 1);
                int pitNumber = Integer.parseInt(stringNumber);
                int destinationPit = Integer.parseInt(stringNumber) + cardValue;
                if (destinationPit > 4) {
                    sendPlayError(color, "Trying to go too far.");
                    return false;
                } else if (destinationPit < 1) {
                    sendPlayError(color, "Can't exit pit.");
                    return false;
                }
                if (board.isPitMovementBlocked(color, destinationPit, pitNumber)) {
                    sendPlayError(color, "Can't skip tokens in pit.");
                    return false;
                }
                board.moveToken(token, color + "PIT" + (destinationPit));
            } else {
                if (board.isPitEntryField(color, Integer.parseInt(tokenPosition)) && cardValue <= 4 && !board.isPitMovementBlocked(color, cardValue, 0)) {
                    board.moveToken(token, color + "PIT" + cardValue);
                } else {
                    int destinationField = (Integer.parseInt(tokenPosition) + cardValue);

                    //check if token can enter pit, then enter pit
                    int destinationPit;
                    if ((destinationPit = board.canEnterPit(token, cardValue)) > 0) {
                        if (!board.isPitMovementBlocked(color, destinationPit, 0)) {
                            board.moveToken(token, color + "PIT" + destinationPit);
                            return true;
                        }
                    }

                    if (board.isBackwardsThroughStartingField(token, destinationField)) {
                        sendPlayError(color, "Can't cross starting field backwards.");
                        return false;
                    }
                    if (destinationField > 64) {
                        destinationField -= 64;
                    }
                    if (destinationField < 1) {
                        destinationField = 65 - cardValue;
                    }
                    if (board.getFieldEntry("" + destinationField) != null) {
                        String tokenOnDestinationfield = board.getFieldEntry("" + destinationField);
                        if (tokenOnDestinationfield.startsWith(color)) {
                            sendPlayError(color, "Field is occupied by other friendly token.");
                            return false;
                        } else {
                            board.sendTokenHome("" + destinationField);
                        }
                    }
                    board.moveToken(token, "" + destinationField);
                }
            }
        }
        return true;

    }

    /**
     * checks if the player specified by color is able to play with the cards he has
     *
     * @param color player specified by color
     * @return if the player is able to play
     */
    private boolean isAbleToPlay(String color) {
        Hand hand = handByColor.get(color);
        if (hand.getSize() == 0) {
            return false;
        }
        for (Card card : hand.getCardsInHand()) {
            if (card.getValue().equals("ACE")) {
                return true;
            }
        }
        for (int i = 1; i < 5; i++) {
            String position = board.getTokenPosition(color + i);
            if (!position.contains("HOME") && !position.contains("PIT")) {
                for (Card card : hand.getCardsInHand()) {
                    if (!card.getValue().equals("JACK")) {
                        return true;
                    } else if (card.getValue().equals("JACK")) {
                        return board.areTokensSwitchable(color);
                    }
                }
            }
            if (position.contains("HOME")) {
                for (Card card : hand.getCardsInHand()) {
                    if (card.getValue().equals("KING")) {
                        return true;
                    }
                }
            }
            if (position.contains("PIT")) {
                int pitNumber = Integer.parseInt(position.substring(position.length() - 1));
                for (Card card : hand.getCardsInHand()) {
                    switch (card.getValue()) {
                        case "TWO":
                            if (pitNumber <= 2) {
                                if (!board.isPitMovementBlocked(color, pitNumber + 2, pitNumber)) {
                                    return true;
                                }
                            }
                            break;
                        case "THREE":
                            if (pitNumber == 1) {
                                if (!board.isPitMovementBlocked(color, pitNumber + 3, pitNumber)) {
                                    return true;
                                }
                            }
                            break;
                        case "FOUR":
                            if (pitNumber <= 3) {
                                if (!board.isPitMovementBlocked(color, pitNumber + 1, pitNumber)) {
                                    return true;
                                }
                            }
                            break;
                    }
                }

            }

        }
        return false;
    }

    /**
     * called by clientThread, will set given command as new action if its the players turn
     *
     * @param command the command sent by the player
     * @return if it is the players turn
     */
    boolean retrievePlayerAction(String command) {
        if (command.startsWith(activePlayer)) {
            nextAction = command;
            return true;
        }
        return false;
    }

    /**
     * checks if a player surrendered and cleans up accordingly
     */
    private void checkForSurrender() {
        if (playersSurrender.size() > 0) {
            for (String color : playersSurrender) {
                turnOrder.remove(color);
                playerNameByColor.remove(color);
                handByColor.remove(color);
            }
        }
        if (turnOrder.size() == 1) {
            for (String color : turnOrder) {
                winner = color;
                clientThreadByName.get(playerNameByColor.get(turnOrder.get(0))).notifyGame();
            }
        }
    }

    /**
     * called by clientThread if a client wants to surrender
     *
     * @param playerName the name of the player
     */
    void playerSurrenders(String playerName) {
        for (String color : playerNameByColor.keySet()) {
            if (playerNameByColor.get(color).equals(playerName)) {
                emptyPlayersHand(color);
                for (int i = 1; i < 5; i++) {
                    board.moveToken(color + i, color + i + "HOME");
                }
                playersSurrender.add(color);
                break;
            }
        }
        clientThreadByName.get(playerName).exitGameAndLobby(true);
        updateTokenPositions(board.getBoardUpdates());
        messageAllPlayers("EXIT;" + playerName);
    }

    private void discardCard(Card card) {
        gameDeck.discardCard(card);
    }

    private void emptyPlayersHand(String color) {
        for (Card card : handByColor.get(color).getCardsInHand()) {
            discardCard(card);
        }
        handByColor.get(color).emptyHand();
        messagePlayerByColor(color, "SET;HAND;EMPTY");
    }


    /**
     * messages all players the new positions of the tokens after an action was processed
     *
     * @param boardUpdate the update given by the board
     */
    private void updateTokenPositions(String boardUpdate) {
        String[] update = boardUpdate.split(";");
        for (int i = 0; i < update.length; i++) {
            messageAllPlayers("SET;" + update[i] + ";" + update[++i]);
        }
    }

    private void updateScoreboard() {
        highscoreList.incrementScore(playerNameByColor.get(winner));
    }

    private void messagePlayerByColor(String color, String msg) {
        if (playerNameByColor.containsKey(color))
            clientThreadByName.get(playerNameByColor.get(color)).receiveMessage("GAME;" + msg);
    }

    private void sendPlayError(String color, String msg) {
        messagePlayerByColor(color, "PLAY;ERROR;" + msg);
    }

    private void messageAllPlayers(String msg) {
        for (ClientThread client : clientThreadByName.values()) {
            client.receiveMessage("GAME;" + msg);
        }
    }

}
