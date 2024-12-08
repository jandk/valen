package be.twofold.valen.ui.viewer.texture;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.op.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.event.*;
import be.twofold.valen.ui.viewer.*;
import jakarta.inject.*;
import javafx.scene.image.*;

public final class TexturePresenter extends AbstractPresenter<TextureView> implements Viewer {
    private byte[] imagePixels;
    private IntPixelOp decoded;
    private WritableImage image;

    private boolean red = true;
    private boolean green = true;
    private boolean blue = true;
    private boolean alpha = true;

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
        decoded = PixelOp.source(surface)
            .asInt()
            .swizzleBGRA();
        decoded.toPixels(width, height, imagePixels);

        long t1 = System.nanoTime();
        image = new WritableImage(width, height);
        image.getPixelWriter().setPixels(
            0, 0, width, height,
            PixelFormat.getByteBgraPreInstance(),
            imagePixels, 0, width * 4
        );

        long t2 = System.nanoTime();
        if (!(red && green && blue && alpha)) {
            filterImage(red, green, blue, alpha);
        }
        getView().setImage(image);

        long t3 = System.nanoTime();
        System.out.printf("Decode: %.1f, Create: %.1f, Filter: %.1f\n", (t1 - t0) / 1e6, (t2 - t1) / 1e6, (t3 - t2) / 1e6);
    }

    private void filterImage(boolean red, boolean green, boolean blue, boolean alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;

        var width = (int) image.getWidth();
        var height = (int) image.getHeight();

        // Check which channels are selected
        IntPixelOp combined;
        if ((red ? 1 : 0) + (green ? 1 : 0) + (blue ? 1 : 0) + (alpha ? 1 : 0) == 1) {
            // Do gray expansion
            var channel = red ? decoded.red() : green ? decoded.green() : blue ? decoded.blue() : decoded.alpha();
            combined = channel.rgba();
        } else {
            // Do color masking
            var rOp = red ? decoded.red() : ChannelOp.constant(0);
            var gOp = green ? decoded.green() : ChannelOp.constant(0);
            var bOp = blue ? decoded.blue() : ChannelOp.constant(0);
            var aOp = alpha ? decoded.alpha() : ChannelOp.constant(255);
            combined = IntPixelOp.combine(rOp, gOp, bOp, aOp);
        }

        combined.toPixels(width, height, imagePixels);

        image.getPixelWriter().setPixels(
            0, 0, width, height,
            PixelFormat.getByteBgraPreInstance(),
            imagePixels, 0, width * 4
        );
    }
}
