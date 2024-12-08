package be.twofold.valen.ui.viewer.texture;

import be.twofold.valen.ui.*;
import be.twofold.valen.ui.event.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.util.*;

public final class TextureFXView implements TextureView, FXView {
    private static final Map<String, Color> ButtonColors = Map.of(
        "Red", Color.RED,
        "Green", Color.GREEN,
        "Blue", Color.BLUE,
        "Alpha", Color.GRAY
    );

    private final VBox view = new VBox();
    private final ToggleButton rButton = new ToggleButton("Red");
    private final ToggleButton gButton = new ToggleButton("Green");
    private final ToggleButton bButton = new ToggleButton("Blue");
    private final ToggleButton aButton = new ToggleButton("Alpha");
    private final ImageView imageView = new ImageView();
    private final ZoomableScrollPane scrollPane = new ZoomableScrollPane(imageView);

    private final SendChannel<TextureViewEvent> channel;

    @Inject
    TextureFXView(EventBus eventBus) {
        this.channel = eventBus.senderFor(TextureViewEvent.class);
        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return view;
    }

    @Override
    public void setImage(Image image) {
        imageView.setImage(image);
        scrollPane.lockZoomToFit();
    }

    private void buildUI() {
        view.setPrefSize(900, 600);

        // .setFill(new ImagePattern(new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAABlBMVEW/v7////+Zw/90AAAAD0lEQVR4XmNg+I8V4RIGAH6/D/EjO09fAAAAAElFTkSuQmCC")));
        var buttons = List.of(rButton, gButton, bButton, aButton);
        for (var button : buttons) {
            button.setSelected(true);
            button.setOnAction(_ -> fireColorEvent());
            String unselected = ButtonColors.get(button.getText()).toString().replace("0x", "#");
            button.setStyle("-fx-base: " + unselected + ";");

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

        var buttonBox = new ToolBar();
        buttonBox.getItems().addAll(buttons);
        // buttonBox.getItems().add(new Separator());
        // buttonBox.getItems().add(new Slider(-10, 10, 0));
        // buttonBox.getItems().add(new Button("100%"));

        var stackPane = new StackPane();
        stackPane.getChildren().add(new Label("No image loaded"));
        stackPane.getChildren().add(scrollPane);

        VBox.setVgrow(stackPane, Priority.ALWAYS);

        view.getChildren().addAll(buttonBox, stackPane);
    }

    private void fireColorEvent() {
        channel.send(new TextureViewEvent.ColorsToggled(
            rButton.isSelected(),
            gButton.isSelected(),
            bButton.isSelected(),
            aButton.isSelected()
        ));
    }
}
