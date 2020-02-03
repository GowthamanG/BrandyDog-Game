package bdgame.network;

public class Protocol {
    /**
     * @author Michel
     * Protocols use CSV with semicolon as separator.
     * i.e. command;argument 1;...;argument n;
     */


    /**
     * @author Michel
     *         ChatProtocol enum is used for chat interactions.
     */
    public enum ChatProtocol {
        /**
         * @author Michel
         * exit: client wants to leave, close connection. No Arguments
         */
        EXIT,
        /**
         * @author Michel
         * ping: send ping
         */
        PING,
        /**
         * @author cedrik
         * pong: reply to ping
         */
        PONG,
        /**
         * @author Michel
         * show: shows information. Argument chooses what info to show i.e. "show;all" or "show;lobby"
         */
        SHOW,
        /**
         * @author Michel
         * nick chooses new nickname. Argument is new name i.e. "nick;new name"
         */
        NICK,
        /**
         * @author Michel
         * chat: the actual messaging. Argument 1 is target. Argument 2 is message.
         * examples: "chat;user2;hello user2!" "chat;all;hi everybody!"
         */
        CHAT,
        /**
         * @author Michel
         * join: request to join server. Argument is initial nickname
         */
        JOIN,
        /**
         * @author cedrik
         * enter: enters lobby specified by argument (if not existent, creates lobby)
         */
        ENTER,
        /**
         * @author cedrik
         * leave: leaves lobby
         */
        LEAVE,
        /**
         * @author cedrik
         * ready: tell server client is ready to play
         */
        READY,

    }


    /**
     * @author Michel
     *         The actual game protocol. Use this for game specific functions (i.e. dealing cards, moving units...).
     */
    public enum GameProtocol {
        /**
         * start: server sends clients starting game state: START;COLOR
         */
        START,
        /**
         * @author Michel
         * exit: quit the current game. No Arguments
         */
        EXIT,
        /**
         * @author Michel
         * set: multiple uses:
         * SET;CARD;[card] to update last played card
         * SET;CARD;REMOVE;[card] to remove card from player's deck
         * SET;HAND;EMPTY to empty hand
         * SET;TURN;[color];[name] to set whose turn it is
         */
        SET,
        /**
         * get Card from Server
         */
        GET,
        /**
         * inform players that the game is over
         */
        FINISH,
        /**
         * @author Michel
         * PLAY: client plays. Arguments: own color, card to be played, token to be played, special suffix (for cards like 4 or 7)
         */
        PLAY,
        /**
         * Tell clients whose turn it is
         */
        TURN,
        /**
         * @author Michel
         * win: specified player wins the game. Argument is the winning player
         */
        WIN,
    }

}