package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import java.io.Serializable;

/**
 * Constructs a new instance of GameUnoStage. It loads the FXML file for the
 * game
 * interface, sets up the scene, and configures the stage properties.
 * <p>
 * This constructor also sets up the {@code setOnCloseRequest} event handler to
 * automatically save the game state by calling {@code saveGameOnClose()} on the
 * controller when the application window is closed.
 *
 * @throws IOException if an error occurs while loading the FXML file.
 */
/**
 * The {@code GameUno} class implements the core logic for a UNO card game session,
 * managing the state and interactions between a human player, a machine player,
 * the deck of cards, and the table. It provides methods to start the game,
 * handle card drawing, playing cards, managing the "UNO" call, and determining
 * the end of the game.
 * <p>
 * This class implements the {@link IGameUno} interface and is serializable.
 * </p>
 *
 * <p>
 * Key responsibilities include:
 * <ul>
 *   <li>Initializing the game with players, deck, and table</li>
 *   <li>Distributing cards to players at the start</li>
 *   <li>Allowing players to draw cards from the deck</li>
 *   <li>Handling the play of cards onto the table</li>
 *   <li>Managing the "UNO" call and its consequences</li>
 *   <li>Tracking the current turn and determining when the game is over</li>
 * </ul>
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     GameUno game = new GameUno(humanPlayer, machinePlayer, deck, table);
 *     game.startGame();
 * </pre>
 * </p>
 *
 * @author Kyler
 * @version 1.0
 * @see IGameUno
 * @see Player
 * @see Deck
 * @see Table
 */
public class GameUno implements IGameUno, Serializable {
    private static final long serialVersionUID = 1L;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private String currentTurn;

    /**
     * Constructs a new GameUno instance with the specified players, deck, and
     * table.
     *
     * @param humanPlayer   The player representing the human participant
     * @param machinePlayer The player representing the machine participant
     * @param deck          The deck of cards used in the game
     * @param table         The table where the game is played
     */
    public GameUno(Player humanPlayer, Player machinePlayer, Deck deck, Table table) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
        this.table = table;
        this.currentTurn = "HUMAN_PLAYER";
    }

    /**
     * Default constructor for GameUno, initializes players, deck, and table.
     */
    @Override
    public void startGame() {
        for (int i = 0; i < 5; i++) {
            humanPlayer.addCard(this.deck.takeCard());
            machinePlayer.addCard(this.deck.takeCard());
        }
    }
 
    
    @Override
    public void eatCard(Player player, int numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            if (!deck.isEmpty()) {
                player.addCard(this.deck.takeCard());
            }
        }
    }

    @Override
    public void playCard(Card card) {
        this.table.addCardOnTheTable(card);
    }

    @Override
    public void haveSungOne(String playerWhoSang) {
        if (playerWhoSang.equals("HUMAN_PLAYER")) {
            machinePlayer.addCard(this.deck.takeCard());
        } else {
            humanPlayer.addCard(this.deck.takeCard());
        }
    }

    @Override
    public Card[] getCurrentVisibleCardsHumanPlayer(int posInitCardToShow) {
        int totalCards = this.humanPlayer.getCardsPlayer().size();
        int numVisibleCards = Math.min(4, totalCards - posInitCardToShow);
        if (numVisibleCards < 0)
            numVisibleCards = 0;

        Card[] cards = new Card[numVisibleCards];
        for (int i = 0; i < numVisibleCards; i++) {
            cards[i] = this.humanPlayer.getCard(posInitCardToShow + i);
        }
        return cards;
    }

    @Override
    public Boolean isGameOver() {
        return humanPlayer.getCardsPlayer().isEmpty() || machinePlayer.getCardsPlayer().isEmpty();
    }

    public String getCurrentTurn() {
        return this.currentTurn;
    }

    public void setCurrentTurn(String turn) {
        this.currentTurn = turn;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public Player getMachinePlayer() {
        return machinePlayer;
    }

    public Deck getDeck() {
        return deck;
    }

    public Table getTable() {
        return table;
    }
}