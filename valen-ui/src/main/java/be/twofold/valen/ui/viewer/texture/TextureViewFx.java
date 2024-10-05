package be.twofold.valen.ui.viewer.texture;

import be.twofold.valen.ui.*;
import jakarta.inject.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.*;

public final class TextureViewFx extends AbstractView<TextureViewListener> implements TextureView {
    private static final Map<String, String> ButtonColors = Map.of(
        "R", "red",
        "G", "green",
        "B", "blue",
        "A", "grey"
    );

    private final VBox view = new VBox();
    private final ToggleButton rButton = new ToggleButton("R");
    private final ToggleButton gButton = new ToggleButton("G");
    private final ToggleButton bButton = new ToggleButton("B");
    private final ToggleButton aButton = new ToggleButton("A");
    private final ImageView imageView = new ImageView();

    @Inject
    public TextureViewFx() {
        super(TextureViewListener.class);
        buildUI();
    }

    @Override
    public Parent getView() {
        return view;
    }

    @Override
    public void setImage(Image image) {
        imageView.setImage(image);
    }

    private void buildUI() {
        view.setPrefSize(900, 600);

        //        subScene.setFill(new ImagePattern(new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAABlBMVEW/v7////+Zw/90AAAAD0lEQVR4XmNg+I8V4RIGAH6/D/EjO09fAAAAAElFTkSuQmCC")));
        var buttons = List.of(rButton, gButton, bButton, aButton);
        for (var button : buttons) {
            button.setSelected(true);
            button.setOnAction(e -> fireColorEvent());
            button.setStyle("-fx-base: " + ButtonColors.get(button.getText()) + ";");

            button.setOnMouseClicked(e -> {
                if (e.getButton() != MouseButton.SECONDARY) {
                    return;
                }

                for (var toggleButton : buttons) {
                    toggleButton.setSelected(toggleButton == button);
                }
                fireColorEvent();
            });
        }

        imageView.setPreserveRatio(true);
        var scrollPane = new ZoomableScrollPane(imageView);

        var buttonBox = new HBox();
        buttonBox.setPadding(new Insets(5));
        buttonBox.setSpacing(5);
        buttonBox.getChildren().addAll(buttons);

        var stackPane = new StackPane();
        stackPane.getChildren().add(new Label("No image loaded"));
        stackPane.getChildren().add(scrollPane);

        VBox.setVgrow(stackPane, Priority.ALWAYS);

        view.getChildren().addAll(buttonBox, stackPane);
    }

    private void fireColorEvent() {
        listeners().fire().onToggleColor(
            rButton.isSelected(),
            gButton.isSelected(),
            bButton.isSelected(),
            aButton.isSelected()
        );
    }
}
