package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.model.card.Card;

import java.util.Collections;
import java.util.Stack;

/**
 * Represents a deck of Uno cards.
 */
public class Deck {
    private Stack<Card> deckOfCards;

    /**
     * Constructs a new deck of Uno cards and initializes it.
     */
    public Deck() {
        this.deckOfCards = new Stack<>();
        initializeDeck();
        Collections.shuffle(deckOfCards);
    }

    /**
     * Initializes the deck with cards using the CardFactory.
     */
    private void initializeDeck() {
        CardFactory factory = new CardFactory();
        for (EISCUnoEnum cardEnum : EISCUnoEnum.values()) {
            // Avoid adding placeholder or background images to the deck
            if (cardEnum.name().contains("CARD") || cardEnum.name().contains("BACKGROUND") || cardEnum.name().contains("BUTTON") || cardEnum.name().contains("FAVICON") || cardEnum.name().contains("UNO")) {
                continue;
            }
            deckOfCards.push(factory.createCard(cardEnum));
            // Numbered cards (1-9) and special cards appear twice per color
            if (isDuplicatedCard(cardEnum.name())) {
                deckOfCards.push(factory.createCard(cardEnum));
            }
        }
    }
    
    /**
     * Checks if a card type should be duplicated in the deck.
     * @param name The name of the card from the enum.
     * @return True if the card should be duplicated.
     */
    private boolean isDuplicatedCard(String name) {
        return !name.contains("_0") && (name.startsWith("GREEN_") ||
                name.startsWith("YELLOW_") ||
                name.startsWith("BLUE_") ||
                name.startsWith("RED_") ||
                name.startsWith("SKIP_") ||
                name.startsWith("RESERVE_") ||
                name.startsWith("TWO_WILD_DRAW_"));
    }

    /**
     * Takes a card from the top of the deck.
     *
     * @return the card from the top of the deck
     * @throws IllegalStateException if the deck is empty
     */
    public Card takeCard() {
        if (deckOfCards.isEmpty()) {
            // In a real game, the discard pile would be shuffled into a new deck.
            // For this project, we throw an exception.
            throw new IllegalStateException("No hay más cartas en el mazo.");
        }
        return deckOfCards.pop();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return deckOfCards.isEmpty();
    }

    /**
     * Inner class implementing the Factory pattern to create cards.
     * This centralizes the logic for creating card objects from the enum.
     */
    private static class CardFactory {
        public Card createCard(EISCUnoEnum cardEnum) {
            String name = cardEnum.name();
            String path = cardEnum.getFilePath();
            String color = parseColor(name);
            String value = parseValue(name);
            String type = parseType(name);

            return new Card(path, value, color, type);
        }

        private String parseColor(String name) {
            if (name.contains("GREEN")) return "GREEN";
            if (name.contains("YELLOW")) return "YELLOW";
            if (name.contains("BLUE")) return "BLUE";
            if (name.contains("RED")) return "RED";
            return "WILD"; // For WILD and WILD_DRAW_FOUR
        }

        private String parseValue(String name) {
            if (name.contains("SKIP")) return "SKIP";
            if (name.contains("RESERVE")) return "REVERSE";
            if (name.contains("TWO_WILD_DRAW")) return "+2";
            if (name.contains("FOUR_WILD_DRAW")) return "+4";
            if (name.contains("WILD")) return "WILD";
            // For number cards, extract the number
            return name.replaceAll("\\D+", ""); // Extracts digits
        }

        private String parseType(String name) {
            if (name.matches(".*\\d.*")) { // If the name contains a digit
                return "NUMBER";
            }
            return "SPECIAL";
        }
    }
}