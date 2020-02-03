package bdgame.game;

import java.util.ArrayList;

/**
 * This class has the methods to deal with deck cards
 * Created by Ben on 19/04/2017.
 */
public class Hand {

    private ArrayList<Card> cardsInHand;

    /**
     * This constructor creates a new List cardsInHand for a new hand object
     */
    public Hand() {
        this.cardsInHand = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (cardsInHand.size() < 6)
            cardsInHand.add(card);
    }

    public void removeCard(Card card) {
        if (cardsInHand.size() > 0) {
            cardsInHand.removeIf(cardFromHand -> cardFromHand.toString().equals(card.toString()));
        }
    }

    public Card getCardByString(String cardString) {
        for (Card card : cardsInHand) {
            if (card.toString().equals(cardString)) {
                return card;
            }
        }
        return null;
    }

    public int getSize() {
        return cardsInHand.size();
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    /**
     * Removes all of the elements from this list cardsInHand.
     */
    public void emptyHand() {
        cardsInHand.clear();
    }
}

