package be.twofold.valen.ui.viewer.texture;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.viewer.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.image.*;

import java.nio.*;

public final class TexturePresenter extends AbstractPresenter<TextureView> implements Viewer {
    private Image sourceImage;
    private WritableImage targetImage;

    @Inject
    public TexturePresenter(TextureView view) {
        // TODO: Make package-private
        super(view);
        view.addListener(new Listener());
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.Texture;
    }

    @Override
    public void setData(Object data) {
        if (data == null) {
            getView().setImage(null);
            sourceImage = null;
            targetImage = null;
            return;
        }
        var texture = (Texture) data;
        var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.B8G8R8A8_UNORM);

        var pixelBuffer = new PixelBuffer<>(
            texture.width(),
            texture.height(),
            ByteBuffer.wrap(converted.data()),
            PixelFormat.getByteBgraPreInstance()
        );
        sourceImage = new WritableImage(pixelBuffer);
        targetImage = new WritableImage((int) sourceImage.getWidth(), (int) sourceImage.getHeight());
        filterImage(true, true, true, true);
        getView().setImage(targetImage);
    }

    @Override
    public Node getNode() {
        return getView().getView();
    }

    @Override
    public String getName() {
        return "Texture";
    }


    private void filterImage(boolean red, boolean green, boolean blue, boolean alpha) {
        var width = (int) sourceImage.getWidth();
        var height = (int) sourceImage.getHeight();

        // Check which channels are selected

        if (red && green && blue && alpha) {
            targetImage.getPixelWriter().setPixels(0, 0, width, height, sourceImage.getPixelReader(), 0, 0);
            return;
        }

        var sourcePixels = new int[width * height];
        sourceImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), sourcePixels, 0, width);

        var targetPixels = new int[width * height];
        var writer = targetImage.getPixelWriter();

        var numChannels = (red ? 1 : 0) + (green ? 1 : 0) + (blue ? 1 : 0) + (alpha ? 1 : 0);
        if (numChannels == 1) {
            // Do gray expansion
            var sourceChannel = red ? 2 : green ? 1 : blue ? 0 : 3;
            for (int y = 0, i = 0; y < height; y++) {
                for (var x = 0; x < width; x++, i++) {
                    var argb = sourcePixels[i];
                    var v = (argb >> sourceChannel * 8) & 0xFF;
                    var newArgb = 0xFF000000 | (v << 16) | (v << 8) | v;
                    targetPixels[i] = newArgb;
                }
            }
        } else {
            // Do color expansion
            for (int y = 0, i = 0; y < height; y++) {
                for (var x = 0; x < width; x++, i++) {
                    var argb = sourcePixels[i];
                    var a = alpha ? (argb >> 24) & 0xFF : 0xFF;
                    var r = red ? (argb >> 16) & 0xFF : 0;
                    var g = green ? (argb >> +8) & 0xFF : 0;
                    var b = blue ? (argb >> +0) & 0xFF : 0;
                    var newArgb = (a << 24) | (r << 16) | (g << 8) | b;

                    targetPixels[i] = newArgb;
                }
            }
        }

        targetImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), targetPixels, 0, width);
    }

    private final class Listener implements TextureViewListener {
        @Override
        public void onToggleColor(boolean red, boolean green, boolean blue, boolean alpha) {
            filterImage(red, green, blue, alpha);
        }
    }
}
