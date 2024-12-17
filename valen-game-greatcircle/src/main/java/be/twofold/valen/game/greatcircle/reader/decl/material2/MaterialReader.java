package be.twofold.valen.game.greatcircle.reader.decl.material2;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.fi.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.greatcircle.reader.decl.renderparm.*;
import be.twofold.valen.game.greatcircle.reader.image.*;
import be.twofold.valen.game.greatcircle.resource.*;
import com.google.gson.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public final class MaterialReader implements ResourceReader<Material> {
    private static final Logger log = LoggerFactory.getLogger(MaterialReader.class);
    private static final Map<String, RenderParm> RenderParmCache = new HashMap<>();

    private final GreatCircleArchive archive;
    private final DeclReader declReader;

    public MaterialReader(
        GreatCircleArchive archive,
        DeclReader declReader
    ) {
        this.archive = archive;
        this.declReader = declReader;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.material2;
    }

    @Override
    public Material read(DataSource source, Asset asset) throws IOException {
        var object = declReader.read(source, asset);
        return parseMaterial(object, asset.id().fullName());
    }

    private Material parseMaterial(JsonObject object, String name) throws IOException {
        if (!object.has("RenderLayers")) {
            return new Material(name, List.of());
        }

        var parms = object
            .getAsJsonArray("RenderLayers")
            .get(0).getAsJsonObject()
            .getAsJsonObject("parms");

        var renderParms = new EnumMap<ImageTextureMaterialKind, RenderParm>(ImageTextureMaterialKind.class);
        var filenames = new EnumMap<ImageTextureMaterialKind, String>(ImageTextureMaterialKind.class);
        var options = new EnumMap<ImageTextureMaterialKind, MaterialImageOpts>(ImageTextureMaterialKind.class);

        parseRenderParms(parms, renderParms, filenames, options);

        var references = new ArrayList<TextureReference>();

        renderParms.forEach((kind, parm) -> {
            var opts = options.get(kind);

            var builder = new StringBuilder(filenames.get(kind));
            if (kind == ImageTextureMaterialKind.TMK_SMOOTHNESS) {
                builder
                    .append("$smoothnessnormal=")
                    .append(filenames.get(parm.smoothnessNormalParm));
            }
            mapOptions(builder, kind, parm, opts);

            var filename = builder.toString();
            var resourceKey = ResourceKey.from(filename, ResourceType.image);
            if (!archive.exists(resourceKey)) {
                log.warn("Missing image file: {}", filename);
                return;
            }
            var textureType = mapTextureType(kind);
            var supplier = ThrowingSupplier.lazy(() -> archive.loadAsset(resourceKey, Texture.class));
            references.add(new TextureReference(filename, textureType, supplier));
        });

        return new Material(name, references);
    }

    private TextureType mapTextureType(ImageTextureMaterialKind kind) {
        return switch (kind) {
            case TMK_ALBEDO -> TextureType.Albedo;
            case TMK_SPECULAR -> TextureType.Specular;
            case TMK_NORMAL -> TextureType.Normal;
            case TMK_SMOOTHNESS -> TextureType.Smoothness;
            // case TMK_COVER -> TextureType.Unknown;
            // case TMK_SSSMASK -> TextureType.Unknown;
            // case TMK_COLORMASK -> TextureType.Unknown;
            case TMK_BLOOMMASK -> TextureType.Emissive;
            // case TMK_HEIGHTMAP -> TextureType.Height;
            default -> TextureType.Unknown;
        };
    }

    private void mapOptions(StringBuilder builder, ImageTextureMaterialKind kind, RenderParm renderParm, MaterialImageOpts opts) {
        if (opts != null) {
            if (opts.format() != ImageTextureFormat.FMT_NONE) {
                if (kind.getCode() > 7 && kind != ImageTextureMaterialKind.TMK_BLENDMASK) {
                    builder.append(formatFormat(opts.format()));
                }
            }
            if (opts.atlasPadding() != 0) {
                builder.append(formatAtlasPadding(opts.atlasPadding()));
            }
            if (opts.minMip() != 0) {
                builder.append("$minmip=").append(opts.minMip());
            }
            if (opts.fullScaleBias()) {
                builder.append("$fullscalebias");
            }
            if (renderParm.streamed) {
                builder.append("$streamed");
            }
            if (opts.noMips()) {
                builder.append("$nomips");
            }
            if (opts.fftBloom()) {
                builder.append("$fftbloom");
            }
            builder.append(formatMaterialKind(kind));
        }
    }

    private void parseRenderParms(
        JsonObject parms,
        Map<ImageTextureMaterialKind, RenderParm> renderParms,
        Map<ImageTextureMaterialKind, String> filenames,
        Map<ImageTextureMaterialKind, MaterialImageOpts> options
    ) throws IOException {
        for (var entry : parms.entrySet()) {
            var renderParm = RenderParmCache.get(entry.getKey());
            if (renderParm == null) {
                renderParm = loadRenderParm(entry.getKey());
                RenderParmCache.put(entry.getKey(), renderParm);
            }

            renderParms.put(renderParm.materialKind, renderParm);

            filenames.put(
                renderParm.materialKind,
                entry.getValue().getAsJsonObject()
                    .get("filePath").getAsString()
            );

            options.put(
                renderParm.materialKind,
                parseOptions(entry.getValue().getAsJsonObject()
                    .getAsJsonObject("options"))
            );
        }
    }

    private RenderParm loadRenderParm(String name) throws IOException {
        return archive.loadAsset(ResourceKey.from(name, ResourceType.renderparm), RenderParm.class);
    }

    private MaterialImageOpts parseOptions(JsonObject options) {
        if (options == null) {
            return null;
        }
        var type = ImageTextureType.valueOf(options.get("type").getAsString());
        var filter = ImageTextureFilter.valueOf(options.get("filter").getAsString());
        var repeat = ImageTextureRepeat.valueOf(options.get("repeat").getAsString());
        var format = ImageTextureFormat.valueOf(options.get("format").getAsString());
        var atlasPadding = options.get("atlasPadding").getAsShort();
        var minMip = options.has("minMip") ? options.get("minMip").getAsInt() : 0;
        var fullScaleBias = options.has("fullScaleBias") && options.get("fullScaleBias").getAsBoolean();
        var noMips = options.has("noMips") && options.get("noMips").getAsBoolean();
        var fftBloom = options.has("fftBloom") && options.get("fftBloom").getAsBoolean();

        return new MaterialImageOpts(
            type,
            filter,
            repeat,
            format,
            atlasPadding,
            minMip,
            fullScaleBias,
            noMips,
            fftBloom
        );
    }

    private String formatFormat(ImageTextureFormat format) {
        return switch (format) {
            case FMT_RGBA16F -> "$float";
            case FMT_RGBA8 -> "$rgba8";
            case FMT_ALPHA -> "$alpha";
            case FMT_RG8 -> "$rg8";
            case FMT_BC1 -> "$bc1";
            case FMT_BC3 -> "$bc3";
            case FMT_R8 -> "$r8";
            case FMT_BC6H_UF16 -> "$bc6huf16";
            case FMT_BC7 -> "$bc7";
            case FMT_BC4 -> "$bc4";
            case FMT_BC5 -> "$bc5";
            case FMT_RG16F -> "$rg16f";
            case FMT_RG32F -> "$rg32f";
            case FMT_RGBA8_SRGB -> "$rgba8srgb";
            case FMT_BC1_SRGB -> "$bc1srgb";
            case FMT_BC3_SRGB -> "$bc3srgb";
            case FMT_BC7_SRGB -> "$bc7srgb";
            case FMT_BC6H_SF16 -> "$bc6hsf16";
            case FMT_BC1_ZERO_ALPHA -> "$bc1za";
            default -> throw new IllegalArgumentException("Unknown format: " + format);
        };
    }

    private String formatAtlasPadding(short atlasPadding) {
        return switch (atlasPadding) {
            case 1, 2 -> "$pad2";
            case 3, 4 -> "$pad4";
            case 5, 6, 7, 8 -> "$pad8";
            case 9, 10, 11, 12, 13, 14, 15, 16 -> "$pad16";
            default -> throw new IllegalArgumentException("Unknown atlas padding: " + atlasPadding);
        };
    }

    private String formatMaterialKind(ImageTextureMaterialKind materialKind) {
        return switch (materialKind) {
            case TMK_ALBEDO -> "$mtlkind=albedo";
            case TMK_SPECULAR -> "$mtlkind=specular";
            case TMK_NORMAL -> "$mtlkind=normal";
            case TMK_SMOOTHNESS -> "$mtlkind=smoothness";
            case TMK_COVER -> "$mtlkind=cover";
            case TMK_SSSMASK -> "$mtlkind=sssmask";
            case TMK_COLORMASK -> "$mtlkind=colormask";
            case TMK_BLOOMMASK -> "$mtlkind=bloommask";
            case TMK_HEIGHTMAP -> "$mtlkind=heightmap";
            case TMK_DECALALBEDO -> "$mtlkind=decalalbedo";
            case TMK_DECALNORMAL -> "$mtlkind=decalnormal";
            case TMK_DECALSPECULAR -> "$mtlkind=decalspecular";
            case TMK_LIGHTPROJECT -> "$mtlkind=lightproject";
            case TMK_PARTICLE -> "$mtlkind=particle";
            case TMK_UI -> "$mtlkind=ui";
            case TMK_FONT -> "$mtlkind=font";
            case TMK_LEGACY_FLASH_UI -> "$mtlkind=legacyflashui";
            case TMK_BLENDMASK -> "$mtlkind=blendmask";
            default -> "";
        };
    }
}
