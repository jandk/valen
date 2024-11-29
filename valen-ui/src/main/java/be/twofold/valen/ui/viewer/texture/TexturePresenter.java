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
    private int[] targetPixels;
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
        targetPixels = new int[texture.width() * texture.height()];

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

        var reader = sourceImage.getPixelReader();
        var writer = targetImage.getPixelWriter();
        if (red && green && blue && alpha) {
            writer.setPixels(0, 0, width, height, reader, 0, 0);
            return;
        }

        var numChannels = (red ? 1 : 0) + (green ? 1 : 0) + (blue ? 1 : 0) + (alpha ? 1 : 0);
        if (numChannels == 1) {
            // Do gray expansion
            var channel = red ? 2 : green ? 1 : blue ? 0 : 3;
            for (int y = 0, i = 0; y < height; y++) {
                for (var x = 0; x < width; x++, i++) {
                    var argb = reader.getArgb(x, y);
                    var v = (argb >> channel * 8) & 0xFF;
                    var newArgb = 0xFF000000 | v * 0x010101;
                    targetPixels[i] = newArgb;
                }
            }
        } else {
            // Do color masking
            var mask = (alpha ? 0 : 0xFF000000) | (red ? 0x00FF0000 : 0) | (green ? 0x0000FF00 : 0) | (blue ? 0x000000FF : 0);
            for (int y = 0, i = 0; y < height; y++) {
                for (var x = 0; x < width; x++, i++) {
                    targetPixels[i] = reader.getArgb(x, y) & mask;
                }
            }
        }

        writer.setPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), targetPixels, 0, width);
    }

    private final class Listener implements TextureViewListener {
        @Override
        public void onToggleColor(boolean red, boolean green, boolean blue, boolean alpha) {
            filterImage(red, green, blue, alpha);
        }
    }
}
