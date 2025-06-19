package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.Serializable;

/**
 * Represents a card in the Uno game.
 * This class is serializable to allow game state to be saved.
 */
public class Card implements Serializable {
    private static final long serialVersionUID = 1L; // for serialization

    private final String url;
    private final String value;
    private final String color;
    private final String type; // e.g., NUMBER, SKIP, REVERSE, WILD, etc.

    private transient Image image; // transient for non-serializable JavaFX component
    private transient ImageView cardImageView; // transient for non-serializable JavaFX component

    /**
     * Constructs a Card with the specified attributes.
     *
     * @param url the URL of the card image
     * @param value the value of the card (e.g., "5", "+2", "SKIP")
     * @param color the color of the card (e.g., "RED", "BLUE")
     * @param type the type of card (e.g., "NUMBER", "SPECIAL")
     */
    public Card(String url, String value, String color, String type) {
        this.url = url;
        this.value = value;
        this.color = color;
        this.type = type;
        this.image = new Image(String.valueOf(getClass().getResource(url)));
        this.cardImageView = createCardImageView();
    }

    /**
     * Creates and configures the ImageView for the card.
     *
     * @return the configured ImageView of the card
     */
    private ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    /**
     * Gets the ImageView representation of the card.
     *
     * @return the ImageView of the card
     */
    public ImageView getCard() {
        if (cardImageView == null) {
            this.image = new Image(String.valueOf(getClass().getResource(this.url)));
            this.cardImageView = createCardImageView();
        }
        return cardImageView;
    }

    /**
     * Gets the image of the card.
     *
     * @return the Image of the card
     */
    public Image getImage() {
        if (image == null) {
            this.image = new Image(String.valueOf(getClass().getResource(this.url)));
        }
        return image;
    }

    /**
     * Gets the value of the card.
     * @return the card's value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the color of the card.
     * @return the card's color.
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the type of the card.
     * @return the card's type.
     */
    public String getType() {
        return type;
    }
}