package bdgame.game;

import java.util.HashMap;

/**
 * Created by cedrik on 18.05.2017.
 */
public class Board {
    private HashMap<Integer, String> gameFields; //1,...,64 | COLORX
    private HashMap<String, String> homesAndPits; // COLOR(HOME/PIT)X | COLORX
    private HashMap<String, String> tokens; //COLORX | FIELD
    private HashMap<String, Integer> startingFields; //1,17,33,49 || COLORX
    private HashMap<String, Integer> pitEntryFields;
    private StringBuilder tokenUpdates;

    /**
     * creates board in initial state
     */
    public Board() {
        gameFields = new HashMap<>();
        homesAndPits = new HashMap<>();
        tokens = new HashMap<>();
        startingFields = new HashMap<>();
        pitEntryFields = new HashMap<>();
        tokenUpdates = new StringBuilder();

        for (int i = 1; i < 65; i++) {
            gameFields.put(i, null);
        }


        for (Colors.PlayerColors color : Colors.PlayerColors.values()) {
            for (int i = 1; i < 5; i++) {
                tokens.put(color.name() + i, color.name() + "HOME" + i);
                homesAndPits.put(color.name() + i + "HOME", color.name() + i);
                homesAndPits.put(color.name() + "PIT" + i, null);
            }
        }

        startingFields.put(Colors.PlayerColors.YELLOW.name(), 49);
        startingFields.put(Colors.PlayerColors.RED.name(), 33);
        startingFields.put(Colors.PlayerColors.GREEN.name(), 17);
        startingFields.put(Colors.PlayerColors.BLUE.name(), 1);

        pitEntryFields.put(Colors.PlayerColors.YELLOW.name(), 48);
        pitEntryFields.put(Colors.PlayerColors.RED.name(), 32);
        pitEntryFields.put(Colors.PlayerColors.GREEN.name(), 16);
        pitEntryFields.put(Colors.PlayerColors.BLUE.name(), 64);

    }

    public String getTokenPosition(String token) {
        return tokens.get(token);
    }

    /**
     * gets the token on the field specified
     *
     * @param field the field to get its content
     * @return null or the token
     */
    public String getFieldEntry(String field) {
        if (field.contains("HOME") || field.contains("PIT")) {
            return homesAndPits.get(field);
        } else {
            return gameFields.get(Integer.parseInt(field));
        }
    }

    /**
     * Checks if there are tokens the player can switch
     *
     * @param colorOfSwitcher the player who might try to switch
     * @return true or false
     */
    public boolean areTokensSwitchable(String colorOfSwitcher) {
        for (String token : tokens.keySet()) {
            if (!token.startsWith(colorOfSwitcher)) {
                if (!tokens.get(token).contains("HOME") || !tokens.get(token).contains("PIT")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPitEntryField(String color, int field) {
        return pitEntryFields.get(color) == field;
    }

    public boolean isStartingFieldEmpty(String color) {
        return getFieldEntry("" + startingFields.get(color)) == null || !getFieldEntry("" + startingFields.get(color)).startsWith(color);
    }

    public int getPitEntryFieldForColor(String color) {
        return pitEntryFields.get(color);
    }

    public int getStartingField(String color) {
        return startingFields.get(color);
    }

    /**
     * Moves specified token to starting field, leaving home, using movetoken
     *
     * @param token token to be set to start
     */
    public void setTokenToStart(String token) {
        String color = token.substring(0, token.length() - 1);
        switch (Colors.PlayerColors.valueOf(color)) {
            case RED:
                moveToken(token, "33");
                break;
            case BLUE:
                moveToken(token, "1");
                break;
            case GREEN:
                moveToken(token, "17");
                break;
            case YELLOW:
                moveToken(token, "49");
                break;
        }
    }

    public void sendTokenHome(String field) {
        String token;
        if ((token = gameFields.get(Integer.parseInt(field))) != null) {
            moveToken(token, token + "HOME");
        }
    }

    /**
     * Checks if the specified token (by color and position) can move to the specified pit or
     * if there is a friendly token blocking the way     *
     *
     * @param color          color of the token
     * @param destinationPit the pitnumber he wants to move to
     * @param tokensPit      in which pit he is
     * @return if he can move
     */
    public boolean isPitMovementBlocked(String color, int destinationPit, int tokensPit) {
        boolean blocked = false;
        if (tokensPit == 0) {
            for (int i = destinationPit; i >= 1; i--) {
                if (getFieldEntry(color + "PIT" + i) != null) {
                    blocked = true;
                }
            }
        } else {
            for (int i = destinationPit; i > tokensPit; i--) {
                if (getFieldEntry(color + "PIT" + i) != null) {
                    blocked = true;
                }
            }
        }
        return blocked;
    }


    /**
     * Checks if a token can enter the pit when it passes the field before the starting field
     *
     * @param token     the token passing the starting field
     * @param cardValue how many fields the token will be moved
     * @return the number of the pit he will enter, 0 if he cant enter the pit
     */
    public int canEnterPit(String token, int cardValue) {
        String color = token.substring(0, token.length() - 1);
        int field = Integer.parseInt(getTokenPosition(token));
        switch (Colors.PlayerColors.valueOf(color)) {
            case YELLOW:
                int destination = field + cardValue - 64;
                if (destination > 0) {
                    if (destination <= 4) {
                        return destination;
                    }
                }
                break;
            default:
                for (int i = cardValue; i > 0; i--) {
                    if (field == getPitEntryFieldForColor(color)) {
                        if (i <= 4) {
                            return i;
                        }
                    }
                    field++;
                }
        }
        return 0;
    }

    /**
     * Checking if its tried to move a token backwards over the starting field
     *
     * @param token            the token being moved
     * @param destinationField the destinationfield
     * @return true or false
     */
    public boolean isBackwardsThroughStartingField(String token, int destinationField) {
        String color = token.substring(0, token.length() - 1);
        int startingField = startingFields.get(color);
        int position = Integer.parseInt(getTokenPosition(token));
        switch (Colors.PlayerColors.valueOf(color)) {
            case YELLOW:
                return (destinationField < 1);
            default:
                return (destinationField < startingField && position >= startingField);
        }
    }


    /**
     * moving a token and removing its old position in the hashmaps
     *
     * @param token    the token to be moved
     * @param position the new position
     */
    public void moveToken(String token, String position) {
        String oldPosition = tokens.get(token);
        if (oldPosition.contains("HOME") || oldPosition.contains("PIT")) {
            homesAndPits.put(oldPosition, null);
        } else {
            int field = Integer.parseInt(oldPosition);
            gameFields.put(field, null);
        }
        if (position.contains("HOME") || position.contains("PIT")) {
            homesAndPits.put(position, token);
            tokens.put(token, position);
        } else {
            gameFields.put(Integer.parseInt(position), token);
            tokens.put(token, position);
        }
        tokenUpdates.append(token).append(";").append(getTokenPosition(token)).append(";");

    }


    /**
     * checks if someone won
     *
     * @return null if there is no winner or the color of the winner
     */
    public String getWinner() {
        for (Colors.PlayerColors color : Colors.PlayerColors.values()) {
            boolean won = true;
            for (int i = 1; i < 5; i++) {
                if (!tokens.get(color.name() + i).contains("PIT")) {
                    won = false;
                }
            }
            if (won) {
                return color.name();
            }
        }
        return null;
    }


    /**
     * switches positions of specified tokens
     *
     * @param token1 token1 to be switched with token2
     * @param token2 token2 to be switched with token1
     */
    public void switchTokens(String token1, String token2) {
        String position1 = tokens.get(token1);
        moveToken(token1, tokens.get(token2));
        moveToken(token2, position1);
        tokenUpdates.append(token1).append(";")
                .append(getTokenPosition(token1)).append(";")
                .append(token2).append(";")
                .append(getTokenPosition(token2)).append(";");
    }

    public String getBoardUpdates() {
        String update = tokenUpdates.toString();
        tokenUpdates = new StringBuilder();
        return update;
    }


}
