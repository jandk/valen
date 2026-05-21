package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.*;

import java.util.*;

@Singleton
public final class TextureController extends AbstractView<TextureView.Listener> implements TextureView {
    private static final Map<Channel, String> CHANNEL_NAMES = Map.of(
        Channel.RED, "R",
        Channel.GREEN, "G",
        Channel.BLUE, "B",
        Channel.ALPHA, "A",
        Channel.RGB, "RGB",
        Channel.ALL, "RGBA"
    );

    private @FXML Parent root;
    private @FXML ChoiceBox<Channel> channelBox;
    private @FXML Separator sliceSeparator;
    private @FXML Label sliceLabel;
    private @FXML Spinner<Integer> sliceSpinner;
    private @FXML Label mipLabel;
    private @FXML Spinner<Integer> mipSpinner;
    private @FXML Label statusLabel;
    private @FXML StackPane imageArea;

    private final ImageView imageView = new ImageView();
    private ZoomableScrollPane scrollPane;

    @Inject
    TextureController() {
    }

    @FXML
    private void initialize() {
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
        channelBox.setValue(Channel.ALL);
        channelBox.valueProperty().addListener((_, _, newValue) -> getListener().onChannelSelected(newValue));

        sliceSpinner.valueProperty().addListener((_, _, newValue) -> getListener().onSliceSelected(newValue));
        setSliceCount(1);

        mipSpinner.valueProperty().addListener((_, _, newValue) -> getListener().onMipSelected(newValue));
        setMipCount(1);

        imageView.setPreserveRatio(true);
        scrollPane = new ZoomableScrollPane(imageView);
        imageArea.getChildren().add(scrollPane);
    }

    @Override
    public Parent getFXNode() {
        return root;
    }

    @Override
    public void setImage(Image image, boolean resetZoom) {
        imageView.setImage(image);
        if (resetZoom) {
            scrollPane.lockZoomToFit();
        }
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
}
