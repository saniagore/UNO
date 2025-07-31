package org.example.eiscuno.model.player;

import org.example.eiscuno.model.card.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code Player} class, utilizing TestFX to handle JavaFX components.
 */
class PlayerTest extends ApplicationTest {

    private Player player;
    private Card card1;
    private Card card2;

    /**
     * The {@code start()} method from ApplicationTest initializes the JavaFX toolkit.
     * This is where objects dependent on JavaFX, such as {@code Card} instances, should be created.
     *
     * @param stage The primary stage for the test application.
     */
    @Override
    public void start(Stage stage) {
        card1 = new Card("/org/example/eiscuno/cards-uno/1_blue.png", "1", "BLUE", "NUMBER");
        card2 = new Card("/org/example/eiscuno/cards-uno/2_red.png", "2", "RED", "NUMBER");
    }

    /**
     * This method runs before EACH test to reset the player's state, ensuring test isolation.
     */
    @BeforeEach
    void setUp() {
        player = new Player("HUMAN_PLAYER");
    }



    /**
     * Tests the {@code addCard()} and {@code getCard()} methods to ensure cards are added and retrieved correctly.
     */
    @Test
    void testAddAndGetCard() {
        player.addCard(card1);
        
        assertEquals(1, player.getCardsPlayer().size());
        assertEquals(card1, player.getCard(0));
    }

    /**
     * Tests the {@code removeCard()} method to verify that a card can be successfully removed from the player's hand.
     */
    @Test
    void testRemoveCard() {
        player.addCard(card1);
        player.addCard(card2);
        
        assertEquals(2, player.getCardsPlayer().size());

        player.removeCard(0);

        assertEquals(1, player.getCardsPlayer().size());
        assertEquals(card2, player.getCard(0)); // After removing card1, card2 should be at index 0
    }
    

    /**
     * Tests the {@code getTypePlayer()} method to ensure it returns the correct player type.
     */
    @Test
    void testGetTypePlayer() {
        assertEquals("HUMAN_PLAYER", player.getTypePlayer());
    }
}