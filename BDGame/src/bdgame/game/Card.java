package bdgame.game;

/**
 * This class contains String-Arrays, which contains the deck
 * Created by Ben on 19/04/2017.
 */
public class Card {
    private String value;
    private String suit;

    public Card(String cardString) {
        value = cardString.split(":")[0];
        suit = cardString.split(":")[1];
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return value + ":" + suit;
    }
}
