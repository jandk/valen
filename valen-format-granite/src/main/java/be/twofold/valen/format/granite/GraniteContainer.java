package be.twofold.valen.format.granite;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.format.granite.gdex.*;
import be.twofold.valen.format.granite.gtp.*;
import be.twofold.valen.format.granite.gts.*;
import be.twofold.valen.format.granite.texture.*;
import be.twofold.valen.format.granite.util.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class GraniteContainer {
    private static final Logger log = LoggerFactory.getLogger(GraniteContainer.class);

    private final Map<String, Gtp> gtpCache = new HashMap<>();

    private final Gts gts;
    private final ThrowingFunction<String, BinarySource, IOException> gtpSupplier;
    private final List<TextureInfo> textures;

    private GraniteContainer(Gts gts, ThrowingFunction<String, BinarySource, IOException> gtpSupplier) throws IOException {
        this.gts = Objects.requireNonNull(gts);
        this.gtpSupplier = Objects.requireNonNull(gtpSupplier);
        this.textures = new TextureMapper().mapTextures(gts.metadata());
        log.info("Loaded {} textures from {}", textures.size(), gts.path());
    }

    public static GraniteContainer open(
        BinarySource source,
        String path,
        ThrowingFunction<String, BinarySource, IOException> gtpSupplier
    ) throws IOException {
        return new GraniteContainer(Gts.read(source, path), gtpSupplier);
    }

    public List<TextureInfo> getTextures() {
        return textures;
    }

    // region Exporting

    public Texture read(TextureInfo texture, int layer) throws IOException {
        var tileBorder = gts.header().tileBorder();
        var tileWidth = gts.header().tileWidth() - tileBorder * 2;
        var tileHeight = gts.header().tileHeight() - tileBorder * 2;
        var layerWidth = texture.layers().get(layer).width();
        var layerHeight = texture.layers().get(layer).height();

        int level = -1, tileX = 0, tileY = 0, tileW = 0, tileH = 0;
        for (var i = 0; i < gts.header().levelCount(); i++) {
            tileX = (texture.x() / tileWidth) >>> i;
            tileY = (texture.y() / tileHeight) >>> i;
            tileW = Math.ceilDiv(layerWidth, tileWidth);
            tileH = Math.ceilDiv(layerHeight, tileHeight);
            if (allTilesPresent(tileX, tileW, tileY, tileH, layer, i)) {
                level = i;
                break;
            }
        }
        if (level == -1) {
            throw new IOException("Missing tiles on every level");
        }

        var format = fromLayer(gts.metadata(), layer);
        var tgt = Surface.create(layerWidth, layerHeight, format);

        for (var y = 0; y < tileH; y++) {
            for (var x = 0; x < tileW; x++) {
                var src = readTile(tileX + x, tileY + y, layer, level, format);
                var width = Math.min(tileWidth, layerWidth - x * tileWidth);
                var height = Math.min(tileHeight, layerHeight - y * tileHeight);
                Surface.copy(
                    src, tileBorder, tileBorder,
                    tgt, x * tileWidth, y * tileHeight,
                    width, height
                );
            }
        }

        return Texture.fromSurface(tgt, format);
    }

    private boolean allTilesPresent(int tileX, int numTilesX, int tileY, int numTilesY, int layer, int level) {
        var found = false;
        for (var y = 0; y < numTilesY; y++) {
            for (var x = 0; x < numTilesX; x++) {
                var index = getIndex(tileX + x, tileY + y, layer, level);
                if (index < 0) {
                    if (found) {
                        throw new UnsupportedOperationException("Only some tiles are missing? Wtf?");
                    }
                    return false;
                }
                found = true;
            }
        }
        return true;
    }

    private Surface readTile(int x, int y, int layer, int level, TextureFormat format) throws IOException {
        return readTile(getIndex(x, y, layer, level), format);
    }

    private int getIndex(int x, int y, int layer, int level) {
        var numLayers = gts.layers().size();
        var levelInfo = gts.levels().get(level);
        var tileIndex = (y * levelInfo.width() + x) * numLayers + layer;
        return levelInfo.indices().get(tileIndex);
    }

    private Surface readTile(int index, TextureFormat format) throws IOException {
        var tile = gts.tiles().get(index & 0xFFFFFF);
        // var reverseTile = gts.tileIndex().get(tile.tileOffset());
        var page = gts.pageFiles().get(tile.fileIndex());
        var pagePath = Filenames.getPath(gts.path()) + "/" + page.filename();
        var surfaceSize = format.surfaceSize(
            gts.header().tileWidth(),
            gts.header().tileHeight()
        );

        var gtp = gtpCache.computeIfAbsent(pagePath, path -> {
            try (var source = gtpSupplier.apply(path)) {
                return Gtp.read(source, gts.header().pageSize());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        var chunk = gtp.pages()
            .get(tile.pageIndex())
            .chunks().get(tile.chunkIndex());

        var bytes = switch (chunk.codec()) {
            case UNIFORM -> createUniformTile(chunk, surfaceSize, format);
            case BC -> createBCTile(chunk, surfaceSize);
            default -> throw new UnsupportedOperationException("Unsupported codec: " + chunk.codec());
        };
        return new Surface(gts.header().tileWidth(), gts.header().tileHeight(), format, bytes);
    }

    private byte[] createUniformTile(GtpChunk data, int surfaceSize, TextureFormat format) {
        var r = data.data().get(0);
        var g = data.data().length() > 1 ? data.data().get(1) : 0;
        var b = data.data().length() > 2 ? data.data().get(2) : 0;
        var a = data.data().length() > 3 ? data.data().get(3) : 0;
        Bytes block = switch (format) {
            case BC1_UNORM, BC1_SRGB -> BCConstant.bc1(r, g, b);
            case BC3_UNORM, BC3_SRGB -> BCConstant.bc3(r, g, b, a);
            case BC4_UNORM, BC4_SNORM -> BCConstant.bc4(r);
            case BC5_UNORM, BC5_SNORM -> BCConstant.bc5(r, g);
            case BC7_UNORM, BC7_SRGB -> throw new UnsupportedOperationException("Todo");
            default -> throw new UnsupportedOperationException("Can't create a block for " + format);
        };

        var result = new byte[surfaceSize];
        var bytes = Bytes.Mutable.wrap(result);
        for (var i = 0; i < bytes.length(); i += block.length()) {
            block.copyTo(bytes, i);
        }
        return result;
    }

    private byte[] createBCTile(GtpChunk chunk, int surfaceSize) throws IOException {
        var header = (CodecHeader.BC) gts.codecHeaders().get(chunk.param());
        var decompressor = switch (header.getCompression()) {
            case FAST_LZ -> Decompressor.fastLZ();
            case LZ4 -> Decompressor.lz4Block();
            case RAW -> Decompressor.none();
        };

        // The original code seems to allocate the surface plus an extra mip
        // TODO: Figure out when the mip is present and when it isn't
        return decompressor
            .decompress(chunk.data(), surfaceSize + surfaceSize / 4)
            .slice(0, surfaceSize)
            .toArray();
    }

    private TextureFormat fromLayer(GdexStruct meta, int layer) {
        var type = meta.findOne(GdexItemTag.LINF).asStruct()
            .find(GdexItemTag.LAYR).skip(layer)
            .findFirst().orElseThrow().asStruct()
            .findOne(GdexItemTag.TYPE).asString();

        return switch (type) {
            case "BC3" -> TextureFormat.BC3_SRGB; // TODO: Switch between this and UNORM
            default -> throw new UnsupportedOperationException();
        };
    }

    // endregion

}
