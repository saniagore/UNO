package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.card.Card;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Deck class, using TestFX.
 */
class DeckTest extends ApplicationTest {

    /**
     * The start() method from TestFX initializes the JavaFX environment.
     * It can be empty if you don't need to initialize anything specific here.
     */
    @Override
    public void start(Stage stage) {
        // No specific initialization is needed here, but the method is mandatory.
    }

    /**
     * Tests that the deck is initialized with the correct number of cards (102).
     */
    @Test
    void testDeckInitializationCreatesCorrectNumberOfCards() {
        Deck deck = new Deck();
        int cardCount = 0;
        
        while (!deck.isEmpty()) {
            deck.takeCard();
            cardCount++;
        }
        
        assertEquals(102, cardCount);
    }
    
    /**
     * Tests that taking a card from an empty deck throws an exception.
     */
    @Test
    void testTakeCardOnEmptyDeckThrowsException() {
        Deck deck = new Deck();
        
        while (!deck.isEmpty()) {
            deck.takeCard();
        }

        assertTrue(deck.isEmpty());
        
        assertThrows(IllegalStateException.class, deck::takeCard);
    }

    /**
     * Tests that taking a card returns a valid Card object.
     */
    @Test
    void testTakeCardReturnsValidCard() {
        Deck deck = new Deck();
        Card card = deck.takeCard();
        
        assertNotNull(card);
    }
}