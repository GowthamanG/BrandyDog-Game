package game;

import static org.junit.Assert.*;

import bdgame.game.Board;
import org.junit.Test;

/**
 * Test Class for Board Class in application package.
 * @author Michel
 *
 */
public class BoardTest {

    @Test
    public void enterGameTest() {
        Board testBoard = new Board();
        testBoard.setTokenToStart("RED1");
        assertTrue(testBoard.getTokenPosition("RED1").equals("33"));
        testBoard.setTokenToStart("GREEN2");
        assertTrue(testBoard.getTokenPosition("GREEN2").equals("17"));
        testBoard.setTokenToStart("BLUE3");
        assertTrue(testBoard.getTokenPosition("BLUE3").equals("1"));
        testBoard.setTokenToStart("YELLOW4");
        assertTrue(testBoard.getTokenPosition("YELLOW4").equals("49"));
    }//End of enterGameTest

    @Test
    public void movementTest() {
        Board testBoard = new Board();
        testBoard.setTokenToStart("RED1");
        testBoard.moveToken("RED1", "44");//moving to a few positions
        assertTrue(testBoard.getTokenPosition("RED1").equals("44"));
        testBoard.moveToken("RED1", "1");
        assertTrue(testBoard.getTokenPosition("RED1").equals("1"));
        testBoard.moveToken("RED1", "REDPIT1");//moving into a pit
        assertTrue(testBoard.getTokenPosition("RED1").equals("REDPIT1"));
        testBoard.moveToken("RED1", "REDHOME1");//moving home (gettin' kicked)
        assertTrue(testBoard.getTokenPosition("RED1").equals("REDHOME1"));
    }//End of movementtest

    @Test
    public void winnerTest() {
        Board testBoard = new Board();
        assertNull(testBoard.getWinner());//No winner at the start of the game
        testBoard.moveToken("RED1", "1");
        testBoard.moveToken("RED2", "24");
        testBoard.moveToken("BLUE2", "25");
        testBoard.moveToken("BLUE3", "31");
        testBoard.moveToken("GREEN3", "40");
        testBoard.moveToken("GREEN4", "2");
        testBoard.moveToken("YELLOW4", "11");
        testBoard.moveToken("YELLOW1", "61");
        assertNull(testBoard.getWinner());//No winner as of yet
        testBoard.moveToken("RED1", "REDPIT1");
        testBoard.moveToken("RED2", "REDPIT2");
        testBoard.moveToken("RED3", "REDPIT3");
        testBoard.moveToken("RED4", "REDPIT4");
        assertTrue(testBoard.getWinner().equals("RED"));
    }//End of winnerTest

    @Test
    public void switchTest() {
        Board testBoard = new Board();
        testBoard.moveToken("RED1", "1");
        testBoard.moveToken("BLUE1", "31");
        assertTrue(testBoard.getTokenPosition("RED1").equals("1")); //Positions were assumed correctly (fisto is pleased)
        assertTrue(testBoard.getTokenPosition("BLUE1").equals("31"));
        testBoard.switchTokens("RED1", "BLUE1");
        assertTrue(testBoard.getTokenPosition("RED1").equals("31")); //Positions were switched correctly
        assertTrue(testBoard.getTokenPosition("BLUE1").equals("1"));
    }//End of switchTest
}
