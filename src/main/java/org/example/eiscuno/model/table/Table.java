package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;
import java.io.Serializable;
import java.util.ArrayList;

public class Table implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Card> cardsTable;
    private String activeColor;

    public Table(){
        this.cardsTable = new ArrayList<>();
    }

    public void addCardOnTheTable(Card card){
        this.cardsTable.add(card);
        if (!card.getColor().equals("WILD")) {
            this.setActiveColor(card.getColor());
        }
    }

    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        if (cardsTable.isEmpty()) {
            throw new IndexOutOfBoundsException("There are no cards on the table.");
        }
        return this.cardsTable.get(this.cardsTable.size()-1);
    }

    public String getActiveColor() { return activeColor; }
    public void setActiveColor(String activeColor) { this.activeColor = activeColor; }
    public ArrayList<Card> getCardsTable() { return cardsTable; } 
}