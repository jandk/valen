package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.scene.image.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;

import java.util.function.*;

public final class TexturePresenter extends AbstractPresenter<TextureView> implements TextureView.Listener, Viewer {
    private static final Logger log = LoggerFactory.getLogger(TexturePresenter.class);
    private final Settings settings;

    private Texture texture;
    private int currentSlice;
    private int currentMip;

    private Surface decoded;
    private Bytes.Mutable imagePixels;
    private Boolean premultiplied;
    private WritableImage image;

    private Channel channel = Channel.ALL;

    @Inject
    public TexturePresenter(TextureView view, Settings settings) {
        // TODO: Make package-private
        super(view);
        this.settings = settings;

        view.setListener(this);
    }

    @Override
    public String getName() {
        return "Texture";
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.TEXTURE;
    }

    @Override
    public void setData(Object data) {
        if (data == null) {
            getView().setImage(null, true);
            getView().setSliceCount(1);
            getView().setMipCount(1);

            texture = null;
            decoded = null;
            imagePixels = null;
            premultiplied = null;
            image = null;
            return;
        }

        texture = (Texture) data;
        currentSlice = 0;
        currentMip = 0;

        getView().setSliceCount(texture.depthOrLayers());
        getView().setMipCount(texture.mipCount());

        decodeAndDisplay(true);
    }

    @Override
    public void onChannelSelected(Channel channel) {
        filterImage(channel);
    }

    @Override
    public void onSliceSelected(int slice) {
        if (currentSlice == slice) {
            return;
        }
        currentSlice = slice;
        decodeAndDisplay(false);
    }

    @Override
    public void onMipSelected(int mip) {
        currentMip = mip;
        if (texture.kind() == TextureKind.TEXTURE_3D) {
            currentSlice = 0;
            getView().setSliceCount(texture.sliceCount(currentMip));
        }
        decodeAndDisplay(false);
    }

    private void decodeAndDisplay(boolean resetZoom) {
        long t0 = System.nanoTime();

        var oldWidth = decoded != null ? decoded.width() : 0;
        decoded = texture
            .convertSurface(currentMip, currentSlice, TextureFormat.B8G8R8A8_SRGB, settings.isReconstructZ())
            .getSurface(0, 0);
        premultiplied = null;

        long t1 = System.nanoTime();

        if (image == null || (int) image.getWidth() != decoded.width() || (int) image.getHeight() != decoded.height()) {
            image = new WritableImage(decoded.width(), decoded.height());
            imagePixels = Bytes.allocate(decoded.width() * decoded.height() * 4);
        }

        filterImage(channel);
        getView().setImage(image, resetZoom);
        if (!resetZoom && oldWidth > 0) {
            getView().adjustScale((double) oldWidth / decoded.width());
        }

        getView().setStatus(buildStatus());

        long t2 = System.nanoTime();
        log.info("Decode: {}, Filter: {}", (t1 - t0) / 1e6, (t2 - t1) / 1e6);
    }

    private String buildStatus() {
        StringBuilder builder = new StringBuilder()
            .append(texture.format())
            .append("\u2009-\u2009")
            .append(Math.max(1, texture.width() >> currentMip));

        if (texture.kind() != TextureKind.TEXTURE_1D) {
            builder.append("x")
                .append(Math.max(1, texture.height() >> currentMip));
        }
        if (texture.kind() == TextureKind.TEXTURE_3D) {
            builder.append("x")
                .append(Math.max(1, texture.depthOrLayers() >> currentMip));
        }

        return builder.toString();
    }

    private boolean detectPremultiplied() {
        var data = decoded.data();
        for (int i = 0; i < data.length(); i += 4) {
            int b = data.getUnsigned(i/**/);
            int g = data.getUnsigned(i + 1);
            int r = data.getUnsigned(i + 2);
            int a = data.getUnsigned(i + 3);
            int max = Math.max(Math.max(r, g), b);
            if (max > a) {
                return false;
            }
        }
        return true;
    }

    private void filterImage(Channel channel) {
        this.channel = channel;

        if (premultiplied == null) {
            premultiplied = !texture.format().hasAlpha() || detectPremultiplied();
        }

        // B8G8R8A8
        IntUnaryOperator operator = switch (channel) {
            case RED -> bgra -> ((bgra >> 16) & 0xFF) * 0x010101 | 0xFF000000;
            case GREEN -> bgra -> ((bgra >> 8) & 0xFF) * 0x010101 | 0xFF000000;
            case BLUE -> bgra -> (bgra & 0xFF) * 0x010101 | 0xFF000000;
            case ALPHA -> bgra -> ((bgra >> 24) & 0xFF) * 0x010101 | 0xFF000000;
            case RGB -> bgra -> (bgra & 0x00FFFFFF) | 0xFF000000;
            case ALL -> premultiplied ? IntUnaryOperator.identity() : this::premultiply;
        };

        var data = decoded.data();
        for (int i = 0; i < data.length(); i += 4) {
            int bgra = data.getInt(i);
            bgra = operator.applyAsInt(bgra);
            imagePixels.setInt(i, bgra);
        }

        var width = (int) image.getWidth();
        var height = (int) image.getHeight();
        image.getPixelWriter().setPixels(
            0, 0, width, height,
            PixelFormat.getByteBgraPreInstance(),
            imagePixels.asMutableBuffer(), width * 4
        );
    }

    private int premultiply(int bgra) {
        int b = (bgra) & 0xFF;
        int g = (bgra >> 8) & 0xFF;
        int r = (bgra >> 16) & 0xFF;
        int a = (bgra >> 24) & 0xFF;
        r = (r * a) >> 8;
        g = (g * a) >> 8;
        b = (b * a) >> 8;
        return a << 24 | r << 16 | g << 8 | b;
    }
}
