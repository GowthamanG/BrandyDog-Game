package game;

import static org.junit.Assert.*;

import bdgame.game.Card;
import bdgame.game.Hand;
import org.junit.Test;

/**
 * Test Class for Hand Class in application package.
 * @author Michel
 *
 */
public class HandTest {

    @Test
    public void HandTest() {
        Hand testHand = new Hand();
        assertTrue(testHand.getSize() == 0);//Hand starts empty
        testHand.addCard(new Card("TWO:HEARTS"));
        assertTrue(testHand.getSize() >0); //Hand is no longer empty
        for(int i = 0; i < 10; i++){
            testHand.addCard(new Card("ACE:SPADES"));
        }
        assertTrue(testHand.getSize() == 6); //Hand can't have more than six cards
        testHand.removeCard(new Card("TWO:HEARTS"));
        assertTrue(testHand.getSize() == 5);//We removed a card
        testHand.emptyHand();
        assertTrue(testHand.getSize() == 0); //We emptied the List
        testHand.addCard(new Card("TWO:HEARTS"));
        testHand.removeCard(new Card("FIVE:CLUBS"));
        assertTrue(testHand.getSize() == 1); //The List shouldn't remove any card if the chosen card isn't in it
    }//End of HandTest
    
}
