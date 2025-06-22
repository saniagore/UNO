package org.example.eiscuno.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import javafx.scene.image.Image;

import javafx.scene.control.ChoiceDialog;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController {

    @FXML
    private GridPane gridPaneCardsMachine;
    @FXML
    private GridPane gridPaneCardsPlayer;
    @FXML
    private ImageView tableImageView;
    @FXML
    private Button buttonUno;
    @FXML
    private Label machineCardCountLabel;
    @FXML
    private Button barajaCard;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;

    private ThreadPlayMachine threadPlayMachine;
    private Thread humanUnoTimerThread;
    private volatile boolean isHumanTurn = true;
    private volatile boolean humanSaidUno = false;

    /**
     * Custom exception for invalid plays.
     */
    private static class InvalidPlayException extends Exception {
        public InvalidPlayException(String message) {
            super(message);
        }
    }

    @FXML
    public void initialize() {
        initVariables();
        gameUno.startGame(); // Reparte cartas a los jugadores. La mesa aún está vacía.

        // --- SOLUCIÓN DIRECTA Y ROBUSTA ---
        // Tomamos la PRIMERA carta disponible del mazo y la ponemos en la mesa.
        // No usamos bucles para evitar cualquier error lógico oculto.
        // Esto garantiza que la mesa tendrá una carta antes de que la UI intente
        // dibujarla.
        if (!deck.isEmpty()) {
            Card firstCard = deck.takeCard();
            table.addCardOnTheTable(firstCard);
        }
        // --- FIN DE LA SOLUCIÓN ---

        // Ahora, con la certeza de que la mesa tiene una carta, actualizamos la vista.
        setupInitialCards();

        // El resto del código para iniciar los hilos no cambia.
        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this);
        threadPlayMachine.setDaemon(true);
        threadPlayMachine.start();

        buttonUno.setVisible(false);
    }

    private void setupInitialCards() {
        // 1. Poner la imagen de la carta inicial en la mesa. ¡ESTA ES LA CORRECCIÓN!
        tableImageView.setImage(table.getCurrentCardOnTheTable().getImage());

        // 2. Mostrar las cartas del jugador humano
        printCardsHumanPlayer();

        // 3. Actualizar el contador de cartas de la máquina
        updateMachineCardCount();

        // 4. Dibujar el reverso de las cartas de la máquina
        gridPaneCardsMachine.getChildren().clear();
        Image backCardImage = new Image(
                Objects.requireNonNull(getClass().getResource(EISCUnoEnum.CARD_UNO.getFilePath())).toString());
        for (int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            ImageView cardImageView = new ImageView(backCardImage);
            cardImageView.setFitHeight(90);
            cardImageView.setFitWidth(70);
            gridPaneCardsMachine.add(cardImageView, i, 0);
        }

        // 1. Obtenemos la ruta de la imagen como un String para usarla en CSS.
        String backCardImagePath = Objects.requireNonNull(getClass().getResource(EISCUnoEnum.CARD_UNO.getFilePath()))
                .toExternalForm();

        // 2. Le damos al BOTÓN el tamaño deseado (no a una ImageView separada).
        barajaCard.setPrefSize(70, 100);

        // 3. Aplicamos el estilo CSS para usar la imagen como fondo.
        barajaCard.setStyle(
                "-fx-background-image: url('" + backCardImagePath + "'); " + // Establece la imagen de fondo.
                        "-fx-background-size: cover; " + // Asegura que la imagen cubra todo el botón.
                        "-fx-background-color: transparent;" // Hace transparente el color de fondo original del botón.
        );
    }

    private void initVariables() {
        humanPlayer = new Player("HUMAN_PLAYER");
        machinePlayer = new Player("MACHINE_PLAYER");
        deck = new Deck();
        table = new Table();
        gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        posInitCardToShow = 0;
        isHumanTurn = true;
    }

    private void printCardsHumanPlayer() {
        gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCards = gameUno.getCurrentVisibleCardsHumanPlayer(posInitCardToShow);

        for (int i = 0; i < currentVisibleCards.length; i++) {
            Card card = currentVisibleCards[i];
            ImageView cardImageView = card.getCard();

            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                if (isHumanTurn) {
                    try {
                        playHumanCard(card);
                    } catch (InvalidPlayException e) {
                        showAlert("Invalid Play", e.getMessage());
                    }
                } else {
                    showAlert("Not Your Turn", "Please wait for the machine to play.");
                }
            });
            gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }

    private void playHumanCard(Card card) throws InvalidPlayException {
        if (!validatePlay(card, table.getCurrentCardOnTheTable())) {
            throw new InvalidPlayException("This card doesn't match the color or value of the card on the table.");
        }

        // Play the card
        isHumanTurn = false;
        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
        gameUno.playCard(card);
        tableImageView.setImage(card.getImage());

        humanSaidUno = false; // Reset UNO status after playing
        buttonUno.setVisible(false);

        printCardsHumanPlayer(); // Update UI

        if (isGameOver()) {
            endGame("You");
            return;
        }

        handleCardEffect(card, humanPlayer);

        // If the turn wasn't skipped, notify the machine thread
        if (!isHumanTurn) { // A skip card would set isHumanTurn back to true
            threadPlayMachine.setMyTurn(true);
        }
    }

    public void handleCardEffect(Card card, Player currentPlayer) {
        String cardValue = card.getValue();
        Player opponent = (currentPlayer == humanPlayer) ? machinePlayer : humanPlayer;
        boolean turnPassedToMachine = false;

        switch (cardValue) {
            case "+2":
                gameUno.eatCard(opponent, 2);
                break;
            case "SKIP":
                // El jugador actual vuelve a jugar, por lo que el turno no pasa a la máquina.
                if (currentPlayer == humanPlayer) {
                    isHumanTurn = true;
                    showAlert("Turn Skipped!", "You get to play again.");
                } else {
                    threadPlayMachine.setMyTurn(true);
                }
                turnPassedToMachine = true; // Técnicamente el turno se procesa de nuevo.
                break;
            case "+4":
                gameUno.eatCard(opponent, 4);
                // El jugador actual debe elegir un color.
                if (currentPlayer == humanPlayer) {
                    showColorPickerDialog();
                    turnPassedToMachine = true; // El turno se pasará dentro del diálogo.
                }
                break;
            case "WILD":
                // El jugador actual debe elegir un color.
                if (currentPlayer == humanPlayer) {
                    showColorPickerDialog();
                    turnPassedToMachine = true; // El turno se pasará dentro del diálogo.
                }
                break;
        }

        // Actualiza la UI para todos los jugadores
        printCardsHumanPlayer();
        updateMachineCardCount();
        checkUnoCondition();

        // Pasa el turno a la máquina SOLO SI no fue un skip o una carta wild
        // (esos casos se manejan de forma especial).
        if (currentPlayer == humanPlayer && !turnPassedToMachine) {
            threadPlayMachine.setMyTurn(true);
        }
    }

    private void checkUnoCondition() {
        if (humanPlayer.getCardsPlayer().size() == 1 && !humanSaidUno) {
            buttonUno.setVisible(true);
            // Start a timer. If UNO is not pressed in time, penalize.
            startUnoTimer();
        } else {
            buttonUno.setVisible(false);
        }

        if (machinePlayer.getCardsPlayer().size() == 1) {
            // Machine automatically "says" UNO
            System.out.println("Machine has ONE card left!");
        }
    }

    private void startUnoTimer() {
        humanUnoTimerThread = new Thread(() -> {
            try {
                // Wait for 2 to 4 seconds
                long sleepTime = 2000 + new Random().nextInt(2001);
                Thread.sleep(sleepTime);

                Platform.runLater(() -> {
                    // If after the time, the player hasn't said UNO and still has one card
                    if (humanPlayer.getCardsPlayer().size() == 1 && !humanSaidUno) {
                        showAlert("UNO Penalty", "You didn't say UNO in time! You draw a card.");
                        gameUno.eatCard(humanPlayer, 1);
                        printCardsHumanPlayer();
                        buttonUno.setVisible(false);
                    }
                });
            } catch (InterruptedException e) {
                // If the thread is interrupted, it means the player said UNO in time.
                Thread.currentThread().interrupt();
            }
        });
        humanUnoTimerThread.setDaemon(true);
        humanUnoTimerThread.start();
    }

    public boolean validatePlay(Card cardToPlay, Card cardOnTable) {
        String activeColor = table.getActiveColor(); // Usamos el color activo de la mesa
        return cardToPlay.getColor().equals("WILD") ||
                cardToPlay.getColor().equals(activeColor) ||
                cardToPlay.getValue().equals(cardOnTable.getValue());
    }

    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < humanPlayer.getCardsPlayer().size(); i++) {
            if (humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    @FXML
    void onHandleTakeCard(ActionEvent event) {
        if (!isHumanTurn) {
            showAlert("Wait!", "It's not your turn.");
            return;
        }
        if (!deck.isEmpty()) {
            gameUno.eatCard(humanPlayer, 1);
            printCardsHumanPlayer();
            isHumanTurn = false;
            threadPlayMachine.setMyTurn(true); // Pass turn to machine
        } else {
            showAlert("Deck Empty", "No more cards to draw.");
        }
    }

    @FXML
    void onHandleUno(ActionEvent event) {
        if (humanPlayer.getCardsPlayer().size() == 1) {
            System.out.println("Human says UNO!");
            humanSaidUno = true;
            buttonUno.setVisible(false);
            if (humanUnoTimerThread != null && humanUnoTimerThread.isAlive()) {
                humanUnoTimerThread.interrupt(); // Stop the penalty timer
            }
        }
    }

    @FXML
    void onHandleExitGame(ActionEvent event) {
        if (humanUnoTimerThread != null && humanUnoTimerThread.isAlive()) {
            humanUnoTimerThread.interrupt(); // Stop the UNO timer if it's running
        }
        // Aquí podrías cerrar la ventana o volver al menú principal.
        // Por simplicidad, solo mostramos un mensaje.
        showAlert("Exit Game", "Thanks for playing! Exiting the game.");
        Platform.exit();
    }

    private void endGame(String winner) {
        showAlert("Game Over", winner + " won the game!");
        isHumanTurn = false; // Stop further plays
        // Potentially disable all buttons
    }

    public boolean isGameOver() {
        boolean isOver = gameUno.isGameOver();
        if (isOver) {
            Platform.runLater(() -> {
                if (humanPlayer.getCardsPlayer().isEmpty()) {
                    endGame("You");
                } else if (machinePlayer.getCardsPlayer().isEmpty()) {
                    endGame("Machine");
                }
            });
        }
        return isOver;
    }

    public void updateMachineCardCount() {
        // Solo intenta actualizar el texto si la etiqueta fue inyectada correctamente
        // desde el FXML
        if (machineCardCountLabel != null) {
            machineCardCountLabel.setText("Machine Cards: " + machinePlayer.getCardsPlayer().size());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getters for threads and game state
    public GameUno getGameUno() {
        return gameUno;
    }

    public void setHumanTurn(boolean isHumanTurn) {
        this.isHumanTurn = isHumanTurn;
    }

    @FXML
    void onHandleBack(ActionEvent event) {
        if (posInitCardToShow > 0) {
            posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    @FXML
    void onHandleNext(ActionEvent event) {
        if (posInitCardToShow < humanPlayer.getCardsPlayer().size() - 4) {
            posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    private void showColorPickerDialog() {
        List<String> colors = Arrays.asList("RED", "GREEN", "BLUE", "YELLOW");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("RED", colors);
        dialog.setTitle("Color Picker");
        dialog.setHeaderText("Choose a color");
        dialog.setContentText("Select the next color:");

        // Muestra el diálogo y espera a que el usuario elija.
        Optional<String> result = dialog.showAndWait();

        // Si el usuario eligió un color, actualiza la mesa.
        result.ifPresent(color -> {
            table.setActiveColor(color);
            // Una vez elegido el color, es el turno de la máquina.
            threadPlayMachine.setMyTurn(true);
        });

        // Si el usuario cierra el diálogo sin elegir, se podría asignar un color por
        // defecto
        // o volver a mostrar el diálogo. Por simplicidad, asumimos que siempre elige.
        if (result.isEmpty()) {
            table.setActiveColor("RED"); // Color por defecto si se cancela
            threadPlayMachine.setMyTurn(true);
        }
    }
}