package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.scene.image.*;
import org.slf4j.*;

import java.util.*;
import java.util.function.*;

public final class TexturePresenter extends AbstractFXPresenter<TextureView> implements Viewer {
    private static final Logger log = LoggerFactory.getLogger(TexturePresenter.class);
    private static final Set<TextureFormat> GRAY = EnumSet.of(
        TextureFormat.R8_UNORM,
        TextureFormat.R16_UNORM,
        TextureFormat.R16_SFLOAT,
        TextureFormat.BC4_UNORM,
        TextureFormat.BC4_SNORM
    );

    private byte[] imagePixels;
    private Texture decoded;
    private WritableImage image;

    private Channel channel = Channel.ALL;

    @Inject
    public TexturePresenter(TextureView view, EventBus eventBus) {
        // TODO: Make package-private
        super(view);

        eventBus
            .receiverFor(TextureViewEvent.class)
            .consume(event -> {
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
        Texture texture = ((Texture) data).firstOnly();


        long t0 = System.nanoTime();
        decoded = texture.convert(TextureFormat.B8G8R8A8_UNORM);
        if (GRAY.contains(texture.format())) {
            splatGray();
        }

        long t1 = System.nanoTime();
        image = new WritableImage(decoded.width(), decoded.height());
        image.getPixelWriter().setPixels(
            0, 0, decoded.width(), decoded.height(),
            PixelFormat.getByteBgraPreInstance(),
            decoded.surfaces().getFirst().data(), 0, decoded.width() * 4
        );
        imagePixels = null;

        long t2 = System.nanoTime();
        if (channel != Channel.ALL) {
            filterImage(channel);
        }
        getView().setImage(image);

        var status = String.format("%s\u2009-\u2009%dx%d", texture.format(), texture.width(), texture.height());
        getView().setStatus(status);

        long t3 = System.nanoTime();
        log.info("Decode: {}, Create: {}, Filter: {}", (t1 - t0) / 1e6, (t2 - t1) / 1e6, (t3 - t2) / 1e6);
    }

    private void splatGray() {
        var data = decoded.surfaces().getFirst().data();
        for (int i = 0; i < data.length; i += 4) {
            int bgra = ByteArrays.getInt(data, i);
            bgra = ((bgra >> 16) & 0xFF) * 0x010101 | (bgra & 0xFF000000);
            ByteArrays.setInt(data, i, bgra);
        }
    }

    private void filterImage(Channel channel) {
        this.channel = channel;

        if (imagePixels == null) {
            imagePixels = new byte[decoded.width() * decoded.height() * 4];
        }

        // B8G8R8A8
        IntUnaryOperator operator = switch (channel) {
            case RED -> rgba -> ((rgba >> 16) & 0xFF) * 0x010101 | 0xFF000000;
            case GREEN -> rgba -> ((rgba >> 8) & 0xFF) * 0x010101 | 0xFF000000;
            case BLUE -> rgba -> (rgba & 0xFF) * 0x010101 | 0xFF000000;
            case ALPHA -> rgba -> ((rgba >> 24) & 0xFF) * 0x010101 | 0xFF000000;
            case RGB -> rgba -> (rgba & 0x00FFFFFF) | 0xFF000000;
            case ALL -> IntUnaryOperator.identity();
        };

        var data = decoded.surfaces().getFirst().data();
        for (int i = 0; i < data.length; i += 4) {
            int bgra = ByteArrays.getInt(data, i);
            bgra = operator.applyAsInt(bgra);
            ByteArrays.setInt(imagePixels, i, bgra);
        }

        var width = (int) image.getWidth();
        var height = (int) image.getHeight();
        image.getPixelWriter().setPixels(
            0, 0, width, height,
            PixelFormat.getByteBgraPreInstance(),
            imagePixels, 0, width * 4
        );
    }
}
