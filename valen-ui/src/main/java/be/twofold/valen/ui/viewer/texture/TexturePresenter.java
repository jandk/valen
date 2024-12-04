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
    private WritableImage image;
    private IntPixelOp decoded;
    private byte[] pixels;

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
            pixels = null;
            return;
        }

        // Let's try our new ops
        var surface = ((Texture) data).surfaces().getFirst();
        decoded = PixelOp.source(surface).asInt();
        var swizzled = decoded
            .swizzleBGRA()
            .toSurface(surface.width(), surface.height());

        image = new WritableImage(surface.width(), surface.height());
        image.getPixelWriter().setPixels(
            0, 0, surface.width(), surface.height(),
            PixelFormat.getByteBgraPreInstance(),
            swizzled.data(), 0, surface.width() * 4
        );
        getView().setImage(image);
    }

    private void filterImage(boolean red, boolean green, boolean blue, boolean alpha) {
        var width = (int) image.getWidth();
        var height = (int) image.getHeight();

        if (pixels == null) {
            pixels = new byte[width * height * 4];
        }

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

        combined
            .swizzleBGRA()
            .toPixels(width, height, pixels);

        image.getPixelWriter().setPixels(
            0, 0, width, height,
            PixelFormat.getByteBgraPreInstance(),
            pixels, 0, width * 4
        );
    }
}
