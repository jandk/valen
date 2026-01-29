package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.util.*;

public final class TextureViewImpl extends AbstractView<TextureView.Listener> implements TextureView {
    private static final Map<Channel, String> CHANNELS = Map.of(
        Channel.RED, "R",
        Channel.GREEN, "G",
        Channel.BLUE, "B",
        Channel.ALPHA, "A",
        Channel.RGB, "RGB",
        Channel.ALL, "RGBA"
    );

    private final VBox view = new VBox();
    private final Label statusLabel = new Label();
    private final ImageView imageView = new ImageView();
    private final ZoomableScrollPane scrollPane = new ZoomableScrollPane(imageView);

    @Inject
    TextureViewImpl() {
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

    @Override
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    private void buildUI() {
        view.setPrefSize(900, 600);


        // .setFill(new ImagePattern(new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAABlBMVEW/v7////+Zw/90AAAAD0lEQVR4XmNg+I8V4RIGAH6/D/EjO09fAAAAAElFTkSuQmCC")));
        var buttons = CHANNELS.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(this::createButton)
            .toList();

        var toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(buttons);
        toggleGroup.selectToggle(toggleGroup.getToggles().getLast()); // lol
        toggleGroup.selectedToggleProperty().addListener((_, oldValue, newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
                return;
            }

            getListener().onChannelSelected((Channel) newValue.getUserData());
        });

        imageView.setPreserveRatio(true);

        var separatorPane = new Pane();
        HBox.setHgrow(separatorPane, Priority.ALWAYS);

        var toolBar = new ToolBar();
        toolBar.getItems().add(new Label("Channel"));
        toolBar.getItems().addAll(buttons);
        toolBar.getItems().add(separatorPane);
        toolBar.getItems().add(statusLabel);
        // toolBar.getItems().add(new Separator());
        // toolBar.getItems().add(new Slider(-10, 10, 0));
        // toolBar.getItems().add(new Button("100%"));

        var stackPane = new StackPane();
        stackPane.getChildren().add(new Label("No image loaded"));
        stackPane.getChildren().add(scrollPane);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        view.getChildren().addAll(toolBar, stackPane);
    }

    private ToggleButton createButton(Map.Entry<Channel, String> e) {
        var button = new ToggleButton(e.getValue());
        button.setUserData(e.getKey());
        return button;
    }

}
