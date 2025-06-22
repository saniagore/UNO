package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the table in a game of EISCUno, where cards are played.
 * This class manages the cards currently on the table and keeps track of the
 * active color.
 */
public class Table implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Card> cardsTable;
    private String activeColor;

    /**
     * Constructs a new Table instance with an empty list of cards.
     */
    public Table() {
        this.cardsTable = new ArrayList<>();
    }

    /**
     * Adds a card to the table and updates the active color if the card is not a
     * wild card.
     *
     * @param card The card to be added to the table.
     */
    public void addCardOnTheTable(Card card) {
        this.cardsTable.add(card);
        if (!card.getColor().equals("WILD")) {
            this.setActiveColor(card.getColor());
        }
    }

    /**
     * Retrieves the current card on the table.
     *
     * @return The last card added to the table.
     * @throws IndexOutOfBoundsException if there are no cards on the table.
     */
    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        if (cardsTable.isEmpty()) {
            throw new IndexOutOfBoundsException("There are no cards on the table.");
        }
        return this.cardsTable.get(this.cardsTable.size() - 1);
    }

    /**
     * Checks if the table is empty.
     *
     * @return true if there are no cards on the table, false otherwise.
     */
    public String getActiveColor() {
        return activeColor;
    }

    /**
     * Sets the active color on the table. This is primarily used after a human
     * player plays a wild card and chooses the next color.
     *
     * @param activeColor the new color to be enforced in the game.
     */
    public void setActiveColor(String activeColor) {
        this.activeColor = activeColor;
    }

    /**
     * Returns the list of cards currently on the table's discard pile.
     *
     * @return An ArrayList of Card objects on the table.
     */
    public ArrayList<Card> getCardsTable() {
        return cardsTable;
    }
}