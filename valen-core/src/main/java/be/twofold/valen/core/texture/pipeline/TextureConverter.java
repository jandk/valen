package be.twofold.valen.core.texture.pipeline;

import be.twofold.valen.core.texture.*;
import org.slf4j.*;

import java.util.*;

public final class TextureConverter {
    private static final Logger log = LoggerFactory.getLogger(TextureConverter.class);

    private TextureConverter() {
    }

    public static Texture convert(Texture src, TextureFormat dstFormat, boolean reconstructZ) {
        var stages = new ArrayList<Stage>();
        if (src.scale() != 1.0f || src.bias() != 0.0f) {
            stages.add(new ScaleAndBias(src.scale(), src.bias()));
        }
        if (reconstructZ && src.format().channelCount() == 2) {
            stages.add(ReconstructZ.INSTANCE);
        }
        var converted = src.surfaces().stream()
            .map(s -> convert(s, dstFormat, stages))
            .toList();
        return src.withFormat(dstFormat).withSurfaces(converted);
    }


    public static Surface convert(Surface source, TextureFormat dstFormat) {
        return convert(source, dstFormat, List.of());
    }

    public static Surface convert(Surface source, TextureFormat dstFormat, List<Stage> stages) {
        if (source.format() == dstFormat && stages.isEmpty()) {
            log.debug("convert {}x{} {} -> no-op", source.width(), source.height(), source.format());
            return source;
        }
        log.debug("convert {}x{} {} -> {} stages=[{}]",
            source.width(), source.height(),
            source.format(), dstFormat,
            stages.stream().map(s -> s.getClass().getSimpleName()).reduce((a, b) -> a + ", " + b).orElse(""));
        var dst = Surface.create(source.width(), source.height(), dstFormat);
        var unpacker = TileUnpacker.forSurface(source);
        var packer = TilePacker.forSurface(dst);
        TileProcessor.process(source.width(), source.height(), unpacker, stages, packer);
        return dst;
    }
}
