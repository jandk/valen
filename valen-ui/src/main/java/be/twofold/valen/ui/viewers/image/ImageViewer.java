package be.twofold.valen.ui.viewers.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.ui.viewers.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class ImageViewer extends VBox implements Viewer {
    private final ToggleButton rButton = new ToggleButton("R");
    private final ToggleButton gButton = new ToggleButton("G");
    private final ToggleButton bButton = new ToggleButton("B");
    private final ToggleButton aButton = new ToggleButton("A");
    private final ImageView imageView = new ImageView();
    private Image sourceImage;
    private WritableImage targetImage;

    public ImageViewer() {
        setPrefSize(900, 600);

        var buttons = List.of(rButton, gButton, bButton, aButton);

        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(5));
        buttonBox.setSpacing(5);
        buttonBox.getChildren().addAll(buttons);

        for (ToggleButton button : buttons) {
            button.setSelected(true);
            button.setOnAction(e -> filterImage());

            button.setOnMouseClicked(e -> {
                if (e.getButton() != MouseButton.SECONDARY) {
                    return;
                }

                for (ToggleButton toggleButton : buttons) {
                    toggleButton.setSelected(toggleButton == button);
                }
                filterImage();
            });
        }

        imageView.setPreserveRatio(true);
        ZoomableScrollPane scrollPane = new ZoomableScrollPane(imageView);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(new Label("No image loaded"));
        stackPane.getChildren().add(scrollPane);

        VBox.setVgrow(stackPane, Priority.ALWAYS);

        getChildren().addAll(buttonBox, stackPane);
    }

    public void setSourceImage(Image sourceImage) {
        this.sourceImage = sourceImage;
        this.targetImage = new WritableImage((int) sourceImage.getWidth(), (int) sourceImage.getHeight());
        filterImage();
        imageView.setImage(targetImage);
    }

    private void filterImage() {
        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        // Check which channels are selected
        boolean rSelected = rButton.isSelected();
        boolean gSelected = gButton.isSelected();
        boolean bSelected = bButton.isSelected();
        boolean aSelected = aButton.isSelected();

        if (rSelected && gSelected && bSelected && aSelected) {
            targetImage.getPixelWriter().setPixels(0, 0, width, height, sourceImage.getPixelReader(), 0, 0);
            return;
        }

        int[] sourcePixels = new int[width * height];
        sourceImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), sourcePixels, 0, width);

        int[] targetPixels = new int[width * height];
        var writer = targetImage.getPixelWriter();

        int numChannels = (rSelected ? 1 : 0) + (gSelected ? 1 : 0) + (bSelected ? 1 : 0) + (aSelected ? 1 : 0);
        if (numChannels == 1) {
            // Do gray expansion
            int sourceChannel = rSelected ? 2 : gSelected ? 1 : bSelected ? 0 : 3;
            for (int y = 0, i = 0; y < height; y++) {
                for (int x = 0; x < width; x++, i++) {
                    int argb = sourcePixels[i];
                    int v = (argb >> sourceChannel * 8) & 0xFF;
                    int newArgb = 0xFF000000 | (v << 16) | (v << 8) | v;
                    targetPixels[i] = newArgb;
                }
            }
        } else {
            // Do color expansion
            for (int y = 0, i = 0; y < height; y++) {
                for (int x = 0; x < width; x++, i++) {
                    int argb = sourcePixels[i];
                    int a = aSelected ? (argb >> 24) & 0xFF : 0xFF;
                    int r = rSelected ? (argb >> 16) & 0xFF : 0;
                    int g = gSelected ? (argb >> +8) & 0xFF : 0;
                    int b = bSelected ? (argb >> +0) & 0xFF : 0;
                    int newArgb = (a << 24) | (r << 16) | (g << 8) | b;

                    targetPixels[i] = newArgb;
                }
            }
        }

        targetImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), targetPixels, 0, width);
    }

    @Override
    public boolean canPreview(Asset asset) {
        return asset.tags().contains(AssetTypeTag.Image);
    }


    @Override
    public boolean setData(Archive archive, Asset asset) throws IOException {
        if (asset == null || archive == null) {
            reset();
            return false;
        }

        Texture texture = (Texture) archive.loadAsset(asset.id());
        Surface converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.B8G8R8A8_UNORM);
        setImage(converted.data(), texture.width(), texture.height());
        return true;
    }

    @Override
    public void reset() {
        imageView.setImage(null);
        sourceImage = null;
        targetImage = null;
    }

    private void setImage(byte[] bgra, int width, int height) {
        var pixelBuffer = new PixelBuffer<>(width, height,
            ByteBuffer.wrap(bgra), PixelFormat.getByteBgraPreInstance());
        WritableImage image = new WritableImage(pixelBuffer);
        setSourceImage(image);
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public String getName() {
        return "Image";
    }
}
