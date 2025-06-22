package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.controller.GameUnoController;

import java.util.Random;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean isMyTurn = false;
    private GameUnoController controller;

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView,
            GameUnoController controller) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.controller = controller;
    }

    public void run() {
        while (true) {
            try {
                if (controller.hasGameEnded()) {
                    break;
                }
                // Non-busy waiting
                synchronized (this) {
                    while (!isMyTurn) {
                        wait();
                    }
                }

                if (controller.isGameOver())
                    break;

                // "Thinking" time
                Thread.sleep(2000);

                // Machine logic to play a card
                playMachineTurn();

                // End of turn
                isMyTurn = false;
                controller.setHumanTurn(true);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    /**
     * Contains the logic for the machine's turn.
     */
    private void playMachineTurn() {
        Card cardToPlay = findPlayableCard();

        if (cardToPlay != null) {
            // Play the card
            Platform.runLater(() -> {
                table.addCardOnTheTable(cardToPlay);
                tableImageView.setImage(cardToPlay.getImage());
                machinePlayer.removeCard(machinePlayer.getCardsPlayer().indexOf(cardToPlay));
                System.out.println("Machine played: " + cardToPlay.getValue() + " " + cardToPlay.getColor());
                controller.handleCardEffect(cardToPlay, machinePlayer);
                controller.updateMachineCardCount();
                if (machinePlayer.getCardsPlayer().size() == 1) {
                    System.out.println("Machine says UNO!");
                }
            });
        } else {
            // Draw a card because no card is playable
            try {
                // Wait between 2 to 4 seconds before drawing
                long sleepTime = 2000 + new Random().nextInt(2001);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Platform.runLater(() -> {
                System.out.println("Machine has no playable cards. Drawing one.");
                controller.getGameUno().eatCard(machinePlayer, 1);
                controller.updateMachineCardCount();
            });
        }
    }

    /**
     * Finds a valid card in the machine's hand to play.
     * 
     * @return a playable Card, or null if none is found.
     */
    private Card findPlayableCard() {
        Card currentCardOnTable = table.getCurrentCardOnTheTable();
        for (Card card : machinePlayer.getCardsPlayer()) {
            if (controller.validatePlay(card, currentCardOnTable)) {
                return card;
            }
        }
        return null;
    }

    /**
     * Sets the turn for the machine. Called by the controller.
     * 
     * @param isMyTurn true to start the machine's turn, false otherwise.
     */
    public void setMyTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
        if (isMyTurn) {
            synchronized (this) {
                notify();
            }
        }
    }
}