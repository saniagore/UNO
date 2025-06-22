// Archivo: Table.java

package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the table in the Uno game where cards are played.
 * Now includes logic for an "active color" to support wild cards.
 */
public class Table implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Card> cardsTable;
    private String activeColor; // NUEVA VARIABLE para el color en juego

    public Table(){
        this.cardsTable = new ArrayList<>();
    }

    /**
     * Adds a card to the table and updates the active color if the card is not wild.
     * @param card The card to be added to the table.
     */
    public void addCardOnTheTable(Card card){
        this.cardsTable.add(card);
        // Si la carta jugada tiene un color (no es una carta Wild negra),
        // ese se convierte en el nuevo color activo.
        if (!card.getColor().equals("WILD")) {
            this.setActiveColor(card.getColor());
        }
    }

    /**
     * Retrieves the current card on the table.
     * @return The card currently on the table.
     * @throws IndexOutOfBoundsException if there are no cards on the table.
     */
    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        if (cardsTable.isEmpty()) {
            throw new IndexOutOfBoundsException("There are no cards on the table.");
        }
        return this.cardsTable.get(this.cardsTable.size() - 1);
    }

    /**
     * Gets the current active color on the table.
     * @return The active color as a String.
     */
    public String getActiveColor() {
        return activeColor;
    }

    /**
     * Sets the active color on the table. This is used after a wild card is played.
     * @param activeColor The color to set as active.
     */
    public void setActiveColor(String activeColor) {
        this.activeColor = activeColor;
        System.out.println("New active color is: " + this.activeColor);
    }
}