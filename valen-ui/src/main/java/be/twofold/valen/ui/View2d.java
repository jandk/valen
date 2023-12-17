package be.twofold.valen.ui;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class View2d extends BorderPane {
    private final ScrollPane scrollPane = new ScrollPane();
    private final ImageView imageView = new ImageView();
    private final Slider slider = new Slider(1.0, 3.0, 1.0);
    private double imageWidth;
    private double imageHeight;

    public View2d() {
        buildView();
    }

    public void setImage(Image image) {
        imageView.setImage(image);
        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
    }

    private void buildView() {
        scrollPane.setPannable(true);

        imageView.setOnMouseEntered(e -> imageView.setCursor(Cursor.OPEN_HAND));
        imageView.setOnMousePressed(e -> imageView.setCursor(Cursor.CLOSED_HAND));
        imageView.setOnMouseReleased(e -> imageView.setCursor(Cursor.OPEN_HAND));
        imageView.setOnMouseExited(e -> imageView.setCursor(Cursor.DEFAULT));

        slider.setBlockIncrement(0.1);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            var x = scrollPane.getHvalue();
            var y = scrollPane.getVvalue();
            imageView.setFitWidth(imageWidth * newValue.doubleValue());
            imageView.setFitHeight(imageHeight * newValue.doubleValue());
            scrollPane.setHvalue(x);
            scrollPane.setVvalue(y);
        });

        getChildren().add(scrollPane);
        setBottom(slider);
    }

    private void reset() {
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
        slider.setValue(1.0);
    }

}
