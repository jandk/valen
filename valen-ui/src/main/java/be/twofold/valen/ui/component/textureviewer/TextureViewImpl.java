package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.*;
import wtf.reversed.toolbox.collect.*;

import java.util.*;

@Singleton
public final class TextureViewImpl extends AbstractView<TextureView.Listener> implements TextureView {
    private static final Map<Channel, String> CHANNEL_NAMES = Map.of(
        Channel.RED, "R",
        Channel.GREEN, "G",
        Channel.BLUE, "B",
        Channel.ALPHA, "A",
        Channel.RGB, "RGB",
        Channel.ALL, "RGBA"
    );

    private final VBox root = new VBox();
    private final ChoiceBox<Channel> channelBox = new ChoiceBox<>();
    private final Separator sliceSeparator = new Separator();
    private final Label sliceLabel = new Label("Slice");
    private final Spinner<Integer> sliceSpinner = new Spinner<>();
    private final Label mipLabel = new Label("Mip");
    private final Spinner<Integer> mipSpinner = new Spinner<>();
    private final Label statusLabel = new Label();
    private final StackPane imageArea = new StackPane();

    private final ImageView imageView = new ImageView();
    private ZoomableScrollPane scrollPane;
    private WritableImage image;

    @Inject
    TextureViewImpl() {
        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return root;
    }

    @Override
    public void setImage(int width, int height, Bytes.Mutable pixels, boolean resetZoom) {
        if (image == null || (int) image.getWidth() != width || (int) image.getHeight() != height) {
            image = new WritableImage(width, height);
            imageView.setImage(image);
        }
        image.getPixelWriter().setPixels(
            0, 0, width, height,
            PixelFormat.getByteBgraPreInstance(),
            pixels.asMutableBuffer(), width * 4
        );
        if (resetZoom) {
            scrollPane.lockZoomToFit();
        }
    }

    @Override
    public void clearImage() {
        image = null;
        imageView.setImage(null);
    }

    @Override
    public void adjustScale(double factor) {
        scrollPane.adjustScale(factor);
    }

    @Override
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void setSliceCount(int count) {
        configureSpinner(sliceLabel, sliceSpinner, count);
    }

    @Override
    public void setMipCount(int count) {
        configureSpinner(mipLabel, mipSpinner, count);
    }

    private void configureSpinner(Label label, Spinner<Integer> spinner, int count) {
        var visible = count > 1;
        label.setVisible(visible);
        label.setManaged(visible);
        spinner.setVisible(visible);
        spinner.setManaged(visible);
        if (visible) {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, count - 1, 0));
        }
        var separatorVisible = sliceSpinner.isVisible() || mipSpinner.isVisible();
        sliceSeparator.setVisible(separatorVisible);
        sliceSeparator.setManaged(separatorVisible);
    }

    // region UI

    private void buildUI() {
        root.setPrefSize(900, 600);

        channelBox.setPrefWidth(60);
        channelBox.getItems().addAll(Channel.values());
        channelBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Channel c) {
                return c != null ? CHANNEL_NAMES.get(c) : "";
            }

            @Override
            public Channel fromString(String s) {
                throw new UnsupportedOperationException();
            }
        });
        // Set the value before wiring the listener: getListener() is null during construction.
        channelBox.setValue(Channel.ALL);
        channelBox.valueProperty().addListener((_, _, newValue) -> getListener().onChannelSelected(newValue));

        mipSpinner.setPrefWidth(70);
        mipSpinner.valueProperty().addListener((_, _, newValue) -> getListener().onMipSelected(newValue));

        sliceSpinner.setPrefWidth(70);
        sliceSpinner.valueProperty().addListener((_, _, newValue) -> getListener().onSliceSelected(newValue));

        var spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        var toolBar = new ToolBar(
            new Label("Channel"), channelBox,
            sliceSeparator,
            mipLabel, mipSpinner,
            sliceLabel, sliceSpinner,
            spacer,
            statusLabel
        );

        imageView.setPreserveRatio(true);
        scrollPane = new ZoomableScrollPane(imageView);
        imageArea.getChildren().add(scrollPane);
        VBox.setVgrow(imageArea, Priority.ALWAYS);

        root.getChildren().setAll(toolBar, imageArea);

        setSliceCount(1);
        setMipCount(1);
    }

    // endregion
}
