package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.Serializable;

/**
     * Re-initializes the transient JavaFX components (Image and ImageView) after deserialization.
     * This method is crucial and must be called after a Card object is loaded from a file
     * to make it visible again on the UI, as JavaFX nodes are not serializable.
*/
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String url;
    private final String value;
    private final String color;
    private final String type;

    private transient Image image;
    private transient ImageView cardImageView;

    public Card(String url, String value, String color, String type) {
        this.url = url;
        this.value = value;
        this.color = color;
        this.type = type;
        reinitializeImageView();
    }

    private ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    public ImageView getCard() {
        if (cardImageView == null) {
            reinitializeImageView();
        }
        return cardImageView;
    }

    public Image getImage() {
        if (image == null) {
            reinitializeImageView();
        }
        return image;
    }

    public void reinitializeImageView() {
        this.image = new Image(String.valueOf(getClass().getResource(this.url)));
        this.cardImageView = createCardImageView();
    }

    public String getValue() { return value; }
    public String getColor() { return color; }
    public String getType() { return type; }
}