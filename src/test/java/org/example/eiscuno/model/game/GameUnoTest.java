package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code GameUno} class. These tests use TestFX for JavaFX environment initialization
 * but focus on the game logic rather than UI interactions.
 */
class GameUnoTest extends ApplicationTest {

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private Card testCard;

    /**
     * The {@code start()} method from TestFX. This is where the JavaFX environment is set up.
     * We initialize the deck and a test card here.
     *
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        deck = new Deck();
        testCard = new Card("/org/example/eiscuno/cards-uno/5_blue.png", "5", "BLUE", "NUMBER");
    }

    /**
     * Sets up the testing environment before each test method.
     * Initializes human and machine players, the table, and a new {@code GameUno} instance.
     */
    @BeforeEach
    void setUp() {
        humanPlayer = new Player("HUMAN_PLAYER");
        machinePlayer = new Player("MACHINE_PLAYER");
        table = new Table();
        gameUno = new GameUno(humanPlayer, machinePlayer, deck, table);
    }


    /**
     * Tests that the {@code startGame()} method deals the correct number of cards (5) to each player.
     */
    @Test
    void testStartGameDealsCorrectNumberOfCards() {
        gameUno.startGame();
        assertEquals(5, humanPlayer.getCardsPlayer().size());
        assertEquals(5, machinePlayer.getCardsPlayer().size());
    }


    /**
     * Tests that the {@code playCard()} method correctly adds the played card to the table.
     */
    @Test
    void testPlayCardAddsCardToTable() {
        gameUno.playCard(testCard);
        assertEquals(testCard, table.getCurrentCardOnTheTable());
    }

    /**
     * Tests the {@code isGameOver()} method.
     * Verifies that the game is not over initially, but becomes over when a player has no cards.
     */
    @Test
    void testIsGameOverWhenPlayerHasNoCards() {
        gameUno.startGame();
        assertFalse(gameUno.isGameOver()); // Game should not be over at the start
        gameUno.getHumanPlayer().getCardsPlayer().clear(); // Simulate a player running out of cards
        assertTrue(gameUno.isGameOver()); // Game should now be over
    }

    /**
     * Tests that the {@code eatCard()} method adds the specified number of cards to a player's hand.
     */
    @Test
    void testEatCardAddsCardsToPlayer() {
        gameUno.eatCard(humanPlayer, 3);
        assertEquals(3, humanPlayer.getCardsPlayer().size());
    }
}