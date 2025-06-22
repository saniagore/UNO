package org.example.eiscuno.model.machine;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

/**
 * ThreadSingUNOMachine is a thread that checks if the human player has only one
 * card left.
 * If so, it prints "UNO" to the console.
 * This thread runs indefinitely, checking at random intervals.
 */
public class ThreadSingUNOMachine implements Runnable {
    private ArrayList<Card> cardsPlayer;

    /**
     * Constructor for ThreadSingUNOMachine.
     * Initializes the thread with the player's cards.
     *
     * @param cardsPlayer The list of cards held by the player.
     */
    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer) {
        this.cardsPlayer = cardsPlayer;
    }

    /**
     * The run method is executed when the thread starts.
     * It sleeps for a random time between 0 and 5000 milliseconds,
     * then checks if the human player has only one card left.
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep((long) (Math.random() * 5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheHumanPlayer();
        }
    }

    /**
     * Checks if the human player has only one card left.
     * If so, it prints "UNO" to the console.
     */
    private void hasOneCardTheHumanPlayer() {
        if (cardsPlayer.size() == 1) {
            System.out.println("UNO");
        }
    }
}