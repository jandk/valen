package be.twofold.valen.ui.viewer.texture;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.op.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.event.*;
import be.twofold.valen.ui.viewer.*;
import jakarta.inject.*;
import javafx.scene.image.*;
import org.slf4j.*;

public final class TexturePresenter extends AbstractPresenter<TextureView> implements Viewer {
    private static final Logger log = LoggerFactory.getLogger(TexturePresenter.class);

    private byte[] imagePixels;
    private U8PixelOp decoded;
    private WritableImage image;

    private boolean showRed = true;
    private boolean showGreen = true;
    private boolean showBlue = true;
    private boolean showAlpha = true;

    @Inject
    public TexturePresenter(TextureView view, EventBus eventBus) {
        // TODO: Make package-private
        super(view);

        eventBus
            .receiverFor(TextureViewEvent.class)
            .consume(event -> {
                switch (event) {
                    case TextureViewEvent.ColorsToggled(var red, var green, var blue, var alpha) ->
                        filterImage(red, green, blue, alpha);
                }
            });
    }

    @Override
    public String getName() {
        return "Texture";
    }

    @Override
    public boolean canPreview(AssetType<?> type) {
        return type == AssetType.TEXTURE;
    }

    @Override
    public void setData(Object data) {
        if (data == null) {
            getView().setImage(null);
            image = null;
            decoded = null;
            imagePixels = null;
            return;
        }

        // Let's try our new ops
        var surface = ((Texture) data).surfaces().getFirst();
        int width = surface.width();
        int height = surface.height();

        imagePixels = new byte[width * height * 4];

        long t0 = System.nanoTime();
        decoded = PixelOp.source(surface).asU8();
        decoded.swizzleBGRA().toPixels(width, height, imagePixels);

        long t1 = System.nanoTime();
        image = new WritableImage(width, height);
        image.getPixelWriter().setPixels(
            0, 0, width, height,
            PixelFormat.getByteBgraPreInstance(),
            imagePixels, 0, width * 4
        );

        long t2 = System.nanoTime();
        if (!(showRed && showGreen && showBlue && showAlpha)) {
            filterImage(showRed, showGreen, showBlue, showAlpha);
        }
        getView().setImage(image);

        long t3 = System.nanoTime();
        log.info("Decode: {}, Create: {}, Filter: {}", (t1 - t0) / 1e6, (t2 - t1) / 1e6, (t3 - t2) / 1e6);
    }

    private void filterImage(boolean red, boolean green, boolean blue, boolean alpha) {
        this.showRed = red;
        this.showGreen = green;
        this.showBlue = blue;
        this.showAlpha = alpha;

        var width = (int) image.getWidth();
        var height = (int) image.getHeight();

        // Check which channels are selected
        U8PixelOp combined;
        if ((red ? 1 : 0) + (green ? 1 : 0) + (blue ? 1 : 0) + (alpha ? 1 : 0) == 1) {
            // Do gray expansion
            var channel = red ? decoded.red() : green ? decoded.green() : blue ? decoded.blue() : decoded.alpha();
            combined = channel.rgba();
        } else {
            // Do color masking
            var rOp = red ? decoded.red() : U8ChannelOp.constant(0);
            var gOp = green ? decoded.green() : U8ChannelOp.constant(0);
            var bOp = blue ? decoded.blue() : U8ChannelOp.constant(0);
            var aOp = alpha ? decoded.alpha() : U8ChannelOp.constant(255);
            combined = U8PixelOp.combine(rOp, gOp, bOp, aOp);
        }

        combined.swizzleBGRA().toPixels(width, height, imagePixels);

        image.getPixelWriter().setPixels(
            0, 0, width, height,
            PixelFormat.getByteBgraPreInstance(),
            imagePixels, 0, width * 4
        );
    }
}
