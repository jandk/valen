package be.twofold.valen.ui.component.textureviewer;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.scene.image.*;
import org.slf4j.*;

import java.util.*;
import java.util.function.*;

public final class TexturePresenter extends AbstractFXPresenter<TextureView> implements Viewer {
    private static final Logger log = LoggerFactory.getLogger(TexturePresenter.class);
    private static final Set<TextureFormat> GRAY = Set.of(
        TextureFormat.R8_UNORM,
        TextureFormat.R16_UNORM,
        TextureFormat.R16_SFLOAT,
        TextureFormat.BC4_UNORM,
        TextureFormat.BC4_SNORM
    );

    private final Settings settings;

    private MutableBytes imagePixels;
    private Texture decoded;
    private boolean premultiplied;
    private WritableImage image;

    private Channel channel = Channel.ALL;

    @Inject
    public TexturePresenter(TextureView view, EventBus backboneEventBus, Settings settings) {
        // TODO: Make package-private
        super(view);
        this.settings = settings;

        backboneEventBus.subscribe(TextureViewEvent.class, event -> {
            switch (event) {
                case TextureViewEvent.ChannelSelected(var selectedChannel) -> filterImage(selectedChannel);
            }
        });
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
            getView().setImage(null);
            image = null;
            decoded = null;
            imagePixels = null;
            return;
        }

        // Let's try our new ops
        long t0 = System.nanoTime();
        Texture texture = ((Texture) data).firstOnly();
        decoded = texture.convert(TextureFormat.B8G8R8A8_UNORM, settings.reconstructZ().get().orElse(false));
        if (GRAY.contains(texture.format())) {
            splatGray();
        }
        premultiplied = !texture.format().hasAlpha() || detectPremultiplied();

        long t1 = System.nanoTime();
        image = new WritableImage(decoded.width(), decoded.height());
        image.getPixelWriter().setPixels(
            0, 0, decoded.width(), decoded.height(),
            PixelFormat.getByteBgraPreInstance(),
            decoded.surfaces().getFirst().data(), 0, decoded.width() * 4
        );
        imagePixels = null;

        long t2 = System.nanoTime();
        if (channel != Channel.ALL || !premultiplied) {
            filterImage(channel);
        }
        getView().setImage(image);

        var status = String.format("%s\u2009-\u2009%dx%d", texture.format(), texture.width(), texture.height());
        getView().setStatus(status);

        long t3 = System.nanoTime();
        log.info("Decode: {}, Create: {}, Filter: {}", (t1 - t0) / 1e6, (t2 - t1) / 1e6, (t3 - t2) / 1e6);
    }

    private void splatGray() {
        var data = MutableBytes.wrap(decoded.surfaces().getFirst().data());
        for (int i = 0; i < data.length(); i += 4) {
            int bgra = data.getInt(i);
            bgra = ((bgra >> 16) & 0xFF) * 0x010101 | (bgra & 0xFF000000);
            data.setInt(i, bgra);
        }
    }

    private boolean detectPremultiplied() {
        var data = decoded.surfaces().getFirst().data();
        for (int i = 0; i < data.length; i += 4) {
            int b = Byte.toUnsignedInt(data[i]);
            int g = Byte.toUnsignedInt(data[i + 1]);
            int r = Byte.toUnsignedInt(data[i + 2]);
            int a = Byte.toUnsignedInt(data[i + 3]);
            int max = Math.max(Math.max(r, g), b);
            if (max > a) {
                return false;
            }
        }
        return true;
    }

    private void filterImage(Channel channel) {
        this.channel = channel;

        if (imagePixels == null) {
            imagePixels = MutableBytes.allocate(decoded.width() * decoded.height() * 4);
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

        var data = Bytes.wrap(decoded.surfaces().getFirst().data());
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
