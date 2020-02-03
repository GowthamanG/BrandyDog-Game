package bdgame.game;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class creates an ArrayList of deck cards and also manages a discard pile
 *
 * @author Gowthaman
 */
public class Deck {

    private ArrayList<Card> deck;
    private ArrayList<Card> discardPile;

    /**
     * This method creates a new array with 52 elements, in each of them its creates a new Card-object
     */
    public Deck() {
        String[] stringValue = new String[]{"TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING", "ACE"};
        String[] stringSuit = new String[]{"HEARTS", "DIAMONDS", "SPADES", "CLUBS"};
        deck = new ArrayList<>();
        for (String value : stringValue) {
            for (String suit : stringSuit) {
                Card card = new Card(value + ":" + suit);
                deck.add(card);
            }
        }
        shuffleDeck();
        discardPile = new ArrayList<>();
    }

    /**
     * It shuffles the Card-array
     */
    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    /**
     * It distributes the cards
     *
     * @return : The dealt Card
     */
    public Card dealCard() {
        if (deck.size() == 0) {
            this.deck.addAll(discardPile);
            this.shuffleDeck();
            discardPile.clear();
        }
        Card card = deck.get(0);
        deck.remove(card);
        return card;
    }

    public void discardCard(Card card) {
        discardPile.add(card);
    }

    public String toString() {
        return "[" + deck.size() + "]" +
                deck.toString() + "\n" + "[" + discardPile.size() + "]" + discardPile.toString();
    }

}