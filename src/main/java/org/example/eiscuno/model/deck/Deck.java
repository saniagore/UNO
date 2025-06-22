package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.model.card.Card;

import java.io.Serializable;
import java.util.Collections;
import java.util.Stack;

public class Deck implements Serializable {
    
    private static final long serialVersionUID = 1L; 

    private Stack<Card> deckOfCards;

    public Deck() {
        this.deckOfCards = new Stack<>();
        initializeDeck();
        Collections.shuffle(deckOfCards);
    }

    private void initializeDeck() {
        CardFactory factory = new CardFactory();
        for (EISCUnoEnum cardEnum : EISCUnoEnum.values()) {
            if (cardEnum.name().contains("CARD") || cardEnum.name().contains("BACKGROUND") || cardEnum.name().contains("BUTTON") || cardEnum.name().contains("FAVICON") || cardEnum.name().contains("UNO") || cardEnum.name().contains("DECK")) {
                continue;
            }
            deckOfCards.push(factory.createCard(cardEnum));
            if (isDuplicatedCard(cardEnum.name())) {
                deckOfCards.push(factory.createCard(cardEnum));
            }
        }
    }
    
    private boolean isDuplicatedCard(String name) {
        return !name.contains("_0") && (name.startsWith("GREEN_") ||
                name.startsWith("YELLOW_") ||
                name.startsWith("BLUE_") ||
                name.startsWith("RED_") ||
                name.startsWith("SKIP_") ||
                name.startsWith("RESERVE_") ||
                name.startsWith("TWO_WILD_DRAW_"));
    }

    public Card takeCard() {
        if (deckOfCards.isEmpty()) {
            throw new IllegalStateException("No hay más cartas en el mazo.");
        }
        return deckOfCards.pop();
    }

    public boolean isEmpty() {
        return deckOfCards.isEmpty();
    }

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
            return "WILD";
        }

        private String parseValue(String name) {
            if (name.contains("SKIP")) return "SKIP";
            if (name.contains("RESERVE")) return "REVERSE";
            if (name.contains("TWO_WILD_DRAW")) return "+2";
            if (name.contains("FOUR_WILD_DRAW")) return "+4";
            if (name.contains("WILD")) return "WILD";
            return name.replaceAll("\\D+", "");
        }

        private String parseType(String name) {
            if (name.matches(".*\\d.*")) {
                return "NUMBER";
            }
            return "SPECIAL";
        }
    }
}