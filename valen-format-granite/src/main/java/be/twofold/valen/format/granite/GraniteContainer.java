package be.twofold.valen.format.granite;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.format.granite.gdex.*;
import be.twofold.valen.format.granite.gtp.*;
import be.twofold.valen.format.granite.gts.*;
import be.twofold.valen.format.granite.util.*;
import be.twofold.valen.format.granite.xml.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class GraniteContainer {
    private static final Logger log = LoggerFactory.getLogger(GraniteContainer.class);

    private final Map<String, Gtp> gtpCache = new HashMap<>();

    private final Gts gts;
    private final ThrowingFunction<String, BinarySource, IOException> gtpSupplier;
    private final List<GraniteTexture> textures;

    private GraniteContainer(Gts gts, ThrowingFunction<String, BinarySource, IOException> gtpSupplier) throws IOException {
        this.gts = Objects.requireNonNull(gts);
        this.gtpSupplier = Objects.requireNonNull(gtpSupplier);
        this.textures = mapTextures(gts.metadata());
        log.info("Loaded {} textures from {}", textures.size(), gts.path());
    }

    public static GraniteContainer open(
        BinarySource source,
        String path,
        ThrowingFunction<String, BinarySource, IOException> gtpSupplier
    ) throws IOException {
        return new GraniteContainer(Gts.read(source, path), gtpSupplier);
    }

    public List<GraniteTexture> getTextures() {
        return textures;
    }

    // region Exporting

    public Texture read(GraniteTexture texture, int layer) throws IOException {
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
                var src = readTile(getIndex(tileX + x, tileY + y, layer, level), format);
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

        if (gtpCache.size() > 5) {
            gtpCache.keySet().removeIf(path -> !path.endsWith("_mips.gtp"));
        }
        var gtp = gtpCache.computeIfAbsent(pagePath, path -> {
            System.out.println("Loading gtp cache " + path);
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

    // region Mapping


    public List<GraniteTexture> mapTextures(GdexStruct metadata) throws IOException {
        var metaTextures = metadata
            .findOne(GdexItemTag.ATLS).asStruct()
            .findOne(GdexItemTag.TXTS).asStruct()
            .find(GdexItemTag.TXTR)
            .map(Gdex::asStruct)
            .toList();

        var rawXml = metadata
            .findOne(GdexItemTag.PROJ).asString();
        var xmlTextureIndex = XmlProject.load(rawXml)
            .importedAssets().stream()
            .collect(Collectors.toUnmodifiableMap(XmlAsset::name, Function.identity()));

        return metaTextures.stream()
            .map(metaTexture -> mapTexture(metaTexture, xmlTextureIndex.get(metaTexture.findOne(GdexItemTag.NAME).asString())))
            .toList();
    }

    private GraniteTexture mapTexture(GdexStruct metaTexture, XmlAsset xmlAsset) {
        var name = metaTexture.findOne(GdexItemTag.NAME).asString();
        var width = metaTexture.findOne(GdexItemTag.WDTH).asNumber().intValue();
        var height = metaTexture.findOne(GdexItemTag.HGHT).asNumber().intValue();
        var x = metaTexture.findOne(GdexItemTag.XXXX).asNumber().intValue();
        var y = metaTexture.findOne(GdexItemTag.YYYY).asNumber().intValue();
        var srgb = metaTexture.findOne(GdexItemTag.SRGB).asArray().stream()
            .map(obj -> (int) obj != 0)
            .toList();

        var layers = IntStream.range(0, xmlAsset.layers().size())
            .mapToObj(i -> mapLayer(xmlAsset.layers().get(i), srgb.get(i)))
            .toList();

        return new GraniteTexture(name, width, height, x, y, layers);
    }

    private GraniteTextureLayer mapLayer(XmlLayer layer, boolean srgb) {
        return layer.textures().size() > 1
            ? mapLayerWithMultipleTextures(layer, srgb)
            : mapLayerWithSingleTexture(layer, srgb);
    }

    private GraniteTextureLayer mapLayerWithSingleTexture(XmlLayer layer, boolean srgb) {
        var texture = layer.textures().getFirst();
        var name = texture.src().substring(texture.src().lastIndexOf('\\') + 1);
        var width = texture.width();
        var height = texture.height();
        return new GraniteTextureLayer(name, width, height, srgb);
    }

    private GraniteTextureLayer mapLayerWithMultipleTextures(XmlLayer layer, boolean srgb) {
        var widths = new HashMap<Integer, Integer>();
        var heights = new HashMap<Integer, Integer>();
        for (var texture : layer.textures()) {
            Check.state(texture.subIndex() == 0, "Can only deal with one subIndex");
            Check.state(texture.arrayIndex() == 0, "Can only deal with one arrayIndex");
            widths.merge(texture.column().orElseThrow(), texture.width(), Math::max);
            heights.merge(texture.row().orElseThrow(), texture.height(), Math::max);
        }
        var texture = layer.textures().getFirst();
        var name = texture.src().substring(texture.src().lastIndexOf('\\') + 1);
        var width = widths.values().stream().mapToInt(Integer::intValue).sum();
        var height = heights.values().stream().mapToInt(Integer::intValue).sum();
        return new GraniteTextureLayer(name, width, height, srgb);
    }

    // endregion

}
