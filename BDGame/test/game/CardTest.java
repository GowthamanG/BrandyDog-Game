package game;

import static org.junit.Assert.*;

import bdgame.game.Card;
import org.junit.Test;

/**
 * Test Class for Card Class in application package. 
 * @author Michel
 *
 */
public class CardTest {

    @Test
    public void CardsTest(){
        //Test each value once and each suit once
        //We're checking toString and getValue at the same time...
        //NOTE: Deprecated assertEquals(String, String) is used since assertArrayEquals(Object, Object) shows up as a bug.
        Card testCard = new Card("TWO:HEARTS");
        assertEquals("TWO:HEARTS",testCard.toString());
        assertEquals("TWO",testCard.getValue());
        testCard = new Card("THREE:HEARTS");
        assertEquals("THREE:HEARTS",testCard.toString());
        assertEquals("THREE",testCard.getValue());
        testCard = new Card("FOUR:HEARTS");
        assertEquals("FOUR:HEARTS",testCard.toString());
        assertEquals("FOUR",testCard.getValue());
        testCard = new Card("FIVE:HEARTS");
        assertEquals("FIVE:HEARTS",testCard.toString());
        assertEquals("FIVE",testCard.getValue());
        testCard = new Card("SIX:DIAMONDS");
        assertEquals("SIX:DIAMONDS",testCard.toString());
        assertEquals("SIX",testCard.getValue());
        testCard = new Card("SEVEN:DIAMONDS");
        assertEquals("SEVEN:DIAMONDS",testCard.toString());
        assertEquals("SEVEN",testCard.getValue());
        testCard = new Card("EIGHT:DIAMONDS");
        assertEquals("EIGHT:DIAMONDS",testCard.toString());
        assertEquals("EIGHT",testCard.getValue());
        testCard = new Card("NINE:SPADES");
        assertEquals("NINE:SPADES",testCard.toString());
        assertEquals("NINE",testCard.getValue());
        testCard = new Card("TEN:SPADES");
        assertEquals("TEN:SPADES",testCard.toString());
        assertEquals("TEN",testCard.getValue());
        testCard = new Card("JACK:SPADES");
        assertEquals("JACK:SPADES",testCard.toString());
        assertEquals("JACK",testCard.getValue());
        testCard = new Card("QUEEN:CLUBS");
        assertEquals("QUEEN:CLUBS",testCard.toString());
        assertEquals("QUEEN",testCard.getValue());
        testCard = new Card("KING:CLUBS");
        assertEquals("KING:CLUBS",testCard.toString());
        assertEquals("KING",testCard.getValue());
        testCard = new Card("ACE:CLUBS");
        assertEquals("ACE:CLUBS",testCard.toString());
        assertEquals("ACE",testCard.getValue());
    }//End of CardsTest

}
