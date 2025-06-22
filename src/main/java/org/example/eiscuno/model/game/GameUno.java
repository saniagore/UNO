package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import java.io.Serializable;

public class GameUno implements IGameUno, Serializable {
    private static final long serialVersionUID = 1L;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private String currentTurn;

    public GameUno(Player humanPlayer, Player machinePlayer, Deck deck, Table table) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
        this.table = table;
        this.currentTurn = "HUMAN_PLAYER";
    }

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
    public void playCard(Card card) { this.table.addCardOnTheTable(card); }

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
        if (numVisibleCards < 0) numVisibleCards = 0;
        
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
    
    public String getCurrentTurn() { return this.currentTurn; }
    public void setCurrentTurn(String turn) { this.currentTurn = turn; }
    public Player getHumanPlayer() { return humanPlayer; }
    public Player getMachinePlayer() { return machinePlayer; }
    public Deck getDeck() { return deck; }
    public Table getTable() { return table; }
}