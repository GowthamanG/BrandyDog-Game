package game;

import static org.junit.Assert.*;
import bdgame.game.Card;
import bdgame.game.Deck;
import org.junit.Test;
/**
 * Test Class for Deck Class in application package.
 * @author Michel
 *
 */
public class DeckTest {

  //Testing shuffle is pointless, so we'll only test dealCard
    @Test
    public void dealCardTest(){
        Deck testDeck = new Deck();
        Object tempCard = testDeck.dealCard();
        assertTrue(tempCard instanceof Card); //We should have been dealt a Card (That's why an Object was declared)
        assertTrue(legalCard((Card)tempCard)); //Card has a legal Value
        testDeck.discardCard((Card)tempCard);
        for(int i = 0; i < 200; i++){
            try{
               tempCard = testDeck.dealCard(); //When the deck runs out it SHOULD be refilled with the discard pile (and shuffeled).
               assertTrue(legalCard((Card)tempCard));//Let's keep testing, while we're at it
               testDeck.discardCard((Card)tempCard); //This can also show us, if the discard pile works or not...
            } catch(IndexOutOfBoundsException e){
                fail("Exception not expected");
            }
            
        }
    }//End of dealCardTest
    
    //Helper Method returns whether card has a legal value
    private boolean legalCard(Card card){
        return card.getValue().equals("TWO")
               || card.getValue().equals("THREE")
               || card.getValue().equals("FOUR")
               || card.getValue().equals("FIVE")
               || card.getValue().equals("SIX")
               || card.getValue().equals("SEVEN")
               || card.getValue().equals("EIGHT")
               || card.getValue().equals("NINE")
               || card.getValue().equals("TEN")
               || card.getValue().equals("JACK")
               || card.getValue().equals("QUEEN")
               || card.getValue().equals("KING")
               || card.getValue().equals("ACE");
    }
}
