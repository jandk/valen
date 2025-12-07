package be.twofold.valen.format.granite;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.export.dds.*;
import be.twofold.valen.format.granite.enums.*;
import be.twofold.valen.format.granite.gdex.*;
import be.twofold.valen.format.granite.gtp.*;
import be.twofold.valen.format.granite.gts.*;
import be.twofold.valen.format.granite.xml.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class GraniteReader {
    private static final Logger log = LoggerFactory.getLogger(GraniteReader.class);

    private final Gts gts;
    private final Function<String, BinaryReader> gtpSupplier;
    private final List<TextureInfo> textures;

    public GraniteReader(Gts gts, Function<String, BinaryReader> gtpSupplier) throws IOException {
        this.gts = Objects.requireNonNull(gts);
        this.gtpSupplier = Objects.requireNonNull(gtpSupplier);
        this.textures = mapTextures(gts);
        System.out.println("Loaded " + textures.size() + " textures");
        log.info("Loaded {} textures", textures.size());
    }

    public List<TextureInfo> getTextures() {
        return textures;
    }

    // region Exporting

    public void exportTexture(TextureInfo texture, int layer) throws IOException {
        var tileBorder = gts.header().tileBorder();
        var tileWidth = gts.header().tileWidth() - tileBorder * 2;
        var tileHeight = gts.header().tileHeight() - tileBorder * 2;
        int layerWidth = texture.layers().get(layer).width();
        int layerHeight = texture.layers().get(layer).height();

        int level = -1, tileX = 0, tileY = 0, tileW = 0, tileH = 0;
        for (int i = 0; i < gts.header().levelCount(); i++) {
            tileX = (texture.x() / tileWidth) >>> i;
            tileY = (texture.y() / tileHeight) >>> i;
            tileW = Math.ceilDiv(layerWidth, tileWidth);
            tileH = Math.ceilDiv(layerHeight, tileHeight);
            if (!checkForMissing(tileX, tileW, tileY, tileH, layer, i)) {
                System.out.println("Found all tiles on level " + i);
                level = i;
                break;
            }
        }
        if (level == -1) {
            throw new IOException("Missing tiles on every level");
        }

        var format = fromLayer(gts.metadata());
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

        var exporter = new DdsExporter();
        var name = texture.layers().get(layer).name();
        name = name.substring(0, name.lastIndexOf('.')) + ".dds";
        var path = Path.of("D:\\Jan\\Desktop\\bg3").resolve(name);
        exporter.export(Texture.fromSurface(tgt, format), path);
    }

    private boolean checkForMissing(int tileX, int numTilesX, int tileY, int numTilesY, int layer, int level) {
        for (int y = 0; y < numTilesY; y++) {
            for (int x = 0; x < numTilesX; x++) {
                var index = getIndex(tileX + x, tileY + y, layer, level);
                if (index < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private final Map<Path, Gtp> GTP_CACHE = new HashMap<>();

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
        var page = gts.pageFiles().get(tile.fileIndex());
        var pagePath = gts.path().getParent().resolve(page.filename());
        var surfaceSize = format.surfaceSize(
            gts.header().tileWidth(),
            gts.header().tileHeight()
        );

        var gtp = GTP_CACHE.computeIfAbsent(pagePath, path -> {
            try (var reader = BinaryReader.fromPath(path)) {
                return Gtp.read(reader, gts.header().pageSize());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        var chunk = gtp.pages()
            .get(tile.pageIndex())
            .chunks().get(tile.chunkIndex());

        var bytes = switch (chunk.codec()) {
            case UNIFORM -> createUniformTile(chunk.data());
            case BC -> createBCTile(chunk);
            default -> throw new UnsupportedOperationException("Unsupported codec: " + chunk.codec());
        };
        if (chunk.codec() == Codec.UNIFORM) {
            // Called UniformCodec, guessing it just fills the image with the specified pixels

            Check.state(chunk.data().length() == 4);
            var pixel = chunk.data().getInt(0);
            var result = new byte[surfaceSize];
//            var bytes = MutableBytes.wrap(result);
//            for (int i = 0; i < bytes.size(); i += 4) {
//                bytes.setInt(i, pixel);
//            }
            return new Surface(gts.header().tileWidth(), gts.header().tileHeight(), format, result);
        }

        var result = new byte[0x10000];
        Decompressor.fastLZ().decompress(chunk.data(), MutableBytes.wrap(result));
        return new Surface(gts.header().tileWidth(), gts.header().tileHeight(), format, result);
    }

    private Bytes createUniformTile(Bytes data) {
        return null;
    }

    private Bytes createBCTile(GtpChunk chunk) {
        // I swear, some of this stuff... We know, from the codec and the tile dimensions, how big the
        var decompressed = new byte[0x10000];
        return Bytes.empty();
    }

    private TextureFormat fromLayer(GdexStruct meta) {
        var type = meta.findOne(GdexItemTag.LINF).asStruct()
            .find(GdexItemTag.LAYR)
            .findFirst().orElseThrow().asStruct()
            .findOne(GdexItemTag.TYPE).asString();

        return switch (type) {
            case "BC3" -> TextureFormat.BC3_SRGB; // TODO: Switch between this and UNORM
            default -> throw new UnsupportedOperationException();
        };
    }

    // endregion

    // region Mapping

    private List<TextureInfo> mapTextures(Gts gts) throws IOException {
        var metaTextures = gts.metadata()
            .findOne(GdexItemTag.ATLS).asStruct()
            .findOne(GdexItemTag.TXTS).asStruct()
            .find(GdexItemTag.TXTR)
            .map(Gdex::asStruct)
            .toList();

        var rawXml = gts.metadata()
            .findOne(GdexItemTag.PROJ).asString();
        var xmlTextureIndex = XmlProject.load(rawXml)
            .importedAssets().stream()
            .collect(Collectors.toUnmodifiableMap(XmlAsset::name, Function.identity()));

        return metaTextures.stream()
            .map(metaTexture -> mapTexture(metaTexture, xmlTextureIndex.get(metaTexture.findOne(GdexItemTag.NAME).asString())))
            .toList();
    }

    private TextureInfo mapTexture(GdexStruct metaTexture, XmlAsset xmlAsset) {
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

        return new TextureInfo(name, width, height, x, y, layers);
    }

    private TextureLayerInfo mapLayer(XmlLayer layer, boolean srgb) {
        return layer.textures().size() > 1
            ? mapLayerWithMultipleTextures(layer, srgb)
            : mapLayerWithSingleTexture(layer, srgb);
    }

    private TextureLayerInfo mapLayerWithSingleTexture(XmlLayer layer, boolean srgb) {
        var texture = layer.textures().getFirst();
        var name = texture.src().substring(texture.src().lastIndexOf('\\') + 1);
        var width = texture.width();
        var height = texture.height();
        return new TextureLayerInfo(name, width, height, srgb);
    }

    private TextureLayerInfo mapLayerWithMultipleTextures(XmlLayer layer, boolean srgb) {
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
        return new TextureLayerInfo(name, width, height, srgb);
    }


    public record TextureInfo(
        String name,
        int width,
        int height,
        int x,
        int y,
        List<TextureLayerInfo> layers
    ) {
    }

    public record TextureLayerInfo(
        String name,
        int width,
        int height,
        boolean srgb
    ) {
    }

    // endregion

}
