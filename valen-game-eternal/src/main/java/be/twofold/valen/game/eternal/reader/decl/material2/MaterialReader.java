package be.twofold.valen.game.eternal.reader.decl.material2;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.*;
import be.twofold.valen.game.eternal.reader.image.*;
import be.twofold.valen.game.eternal.resource.*;
import com.google.gson.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class MaterialReader implements ResourceReader<Material> {
    private static final Logger log = LoggerFactory.getLogger(MaterialReader.class);
    private static final Map<String, RenderParm> RenderParmCache = new HashMap<>();

    private final EternalArchive archive;
    private final DeclReader declReader;

    public MaterialReader(
        EternalArchive archive,
        DeclReader declReader
    ) {
        this.archive = archive;
        this.declReader = declReader;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.RsStreamFile
            && key.name().name().startsWith("generated/decls/material2/");
    }

    @Override
    public Material read(DataSource source, Asset asset) throws IOException {
        var object = declReader.read(source, asset);
        return parseMaterial(object, asset.id().fullName());
    }

    private Material parseMaterial(JsonObject object, String name) throws IOException {
        var materialName = name
            .replace("generated/decls/material2/", "")
            .replace(".decl", "");

        if (!object.has("RenderLayers")) {
            return new Material(materialName, List.of());
        }

        var parms = object
            .getAsJsonArray("RenderLayers")
            .get(0).getAsJsonObject()
            .getAsJsonObject("parms");
        var renderLayerParms = parseRenderLayerParms(parms);
        var standardParms = parseParms(object.getAsJsonObject("Parms"));
        var allParms = Stream.of(renderLayerParms, standardParms)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableMap(Parm::name, r -> r));

        var albedo = mapSimpleTexture(allParms, "albedo", MaterialPropertyType.Albedo);
        var normal = mapSimpleTexture(allParms, "normal", MaterialPropertyType.Normal);
        var specular = mapSimpleTexture(allParms, "specular", MaterialPropertyType.Specular);
        var smoothness = mapSimpleTexture(allParms, "smoothness", MaterialPropertyType.Smoothness);
        var emissive = mapEmissive(allParms);

        var properties = Stream.of(albedo, normal, specular, smoothness, emissive)
            .filter(Objects::nonNull)
            .toList();

//        var properties = new ArrayList<MaterialProperty>();
//        renderLayerParms.forEach(parm -> {
//            MaterialProperty property = mapProperty(parm, allParms);
//            if (property == null) {
//                return;
//            }
//            properties.add(property);
//        });

        return new Material(materialName, properties);
    }

    private MaterialProperty mapEmissive(Map<String, Parm> allParms) {
        var emissive = mapSimpleTexture(allParms, "bloommaskmap", MaterialPropertyType.Emissive);
        var emissiveColor = allParms.get("surfaceemissivecolor") != null ? (Vector3) allParms.get("surfaceemissivecolor").value() : Vector3.One;
        var emissiveScale = allParms.get("surfaceemissivescale") != null ? (Float) allParms.get("surfaceemissivescale").value() : 1.0f;
        Vector4 emissiveFactor = new Vector4(emissiveColor, emissiveScale);

        if (emissive == null) {
            return new MaterialProperty(MaterialPropertyType.Specular, null, emissiveFactor);
        }
        return emissive.withFactor(emissiveFactor);
    }

    private MaterialProperty mapSimpleTexture(Map<String, Parm> parms, String name, MaterialPropertyType propertyType) {
        var albedoParm = parms.get(name);
        if (albedoParm == null) {
            return null;
        }

        var reference = mapTex2D(albedoParm, parms);
        if (reference == null) {
            return null;
        }

        return new MaterialProperty(propertyType, reference, null);
    }

    private TextureReference mapTex2D(Parm parm, Map<String, Parm> allParms) {
        var builder = new StringBuilder(((Tex2D) parm.value).filePath());
        if (parm.renderParm().materialKind == ImageTextureMaterialKind.TMK_SMOOTHNESS) {
            var smoothnessNormal = allParms.entrySet().stream()
                .filter(rlp -> rlp.getValue().renderParm().materialKind == parm.renderParm().smoothnessNormalParm)
                .findFirst().orElseThrow();

            builder
                .append("$smoothnessnormal=")
                .append(((Tex2D) smoothnessNormal.getValue().value()).filePath());
        }
        mapOptions(builder, parm);

        var filename = builder.toString();
        var resourceKey = ResourceKey.from(filename, ResourceType.Image);
        if (!archive.exists(resourceKey)) {
            log.warn("Missing image file: {}", filename);
            return null;
        }

        var supplier = ThrowingSupplier.lazy(() -> archive.loadAsset(resourceKey, Texture.class));
        return new TextureReference(filename, supplier);
    }

    private void mapOptions(StringBuilder builder, Parm parm) {
        var tex2d = (Tex2D) parm.value();
        var opts = tex2d.options();
        var kind = parm.renderParm().materialKind;
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
            if (parm.renderParm().streamed) {
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

    private List<Parm> parseRenderLayerParms(JsonObject parms) throws IOException {
        var renderLayerParms = new ArrayList<Parm>();
        for (var entry : parms.entrySet()) {
            var renderParm = getRenderParm(entry.getKey());
            if (renderParm.isEmpty()) {
                log.warn("Skipping unknown render layer parm: {}", entry.getKey());
                continue;
            }

            var tex2d = parseTex2D(entry);
            renderLayerParms.add(new Parm(entry.getKey(), renderParm.get(), tex2d));
        }
        return renderLayerParms;
    }

    private List<Parm> parseParms(JsonObject parms) throws IOException {
        var result = new ArrayList<Parm>();
        parseParm(parms, "surfaceemissivecolor", e -> parseVector3(e).map(MathF::srgbToLinear))
            .ifPresent(result::add);
        parseParm(parms, "surfaceemissivescale", e -> e.getAsJsonPrimitive().getAsFloat())
            .ifPresent(result::add);
        return result;
    }

    private Optional<Parm> parseParm(JsonObject parms, String name, Function<JsonElement, Object> parser) throws IOException {
        if (!parms.has(name)) {
            return Optional.empty();
        }

        var renderParm = getRenderParm(name);
        if (renderParm.isEmpty()) {
            return Optional.empty();
        }

        var value = parser.apply(parms.get(name));
        return Optional.of(new Parm(name, renderParm.get(), value));
    }

    private Vector3 parseVector3(JsonElement element) {
        var object = element.getAsJsonObject();
        var x = object.getAsJsonPrimitive("x") != null ? object.getAsJsonPrimitive("x").getAsFloat() : 1.0f;
        var y = object.getAsJsonPrimitive("y") != null ? object.getAsJsonPrimitive("y").getAsFloat() : 1.0f;
        var z = object.getAsJsonPrimitive("z") != null ? object.getAsJsonPrimitive("z").getAsFloat() : 1.0f;
        return new Vector3(x, y, z);
    }

    private Optional<RenderParm> getRenderParm(String name) throws IOException {
        if (RenderParmCache.containsKey(name)) {
            return Optional.ofNullable(RenderParmCache.get(name));
        }

        var fullName = "generated/decls/renderparm/" + name + ".decl";
        var resourceKey = ResourceKey.from(fullName, ResourceType.RsStreamFile);
        if (!archive.exists(resourceKey)) {
            RenderParmCache.put(fullName, null);
            return Optional.empty();
        }

        var renderParm = archive.loadAsset(resourceKey, RenderParm.class);
        RenderParmCache.put(name, renderParm);
        return Optional.of(renderParm);
    }

    private Tex2D parseTex2D(Map.Entry<String, JsonElement> entry) {
        var filePath = entry.getValue().getAsJsonObject()
            .get("filePath").getAsString();
        var options = parseOptions(entry.getValue().getAsJsonObject()
            .getAsJsonObject("options"));

        return new Tex2D(filePath, options);
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
            case TMK_LIGHTMAP -> "$mtlkind=lightmap";
            case TMK_UI -> "$mtlkind=ui";
            case TMK_FONT -> "$mtlkind=font";
            case TMK_LEGACY_FLASH_UI -> "$mtlkind=legacyflashui";
            case TMK_LIGHTMAP_DIRECTIONAL -> "$mtlkind=lightmapdir";
            case TMK_BLENDMASK -> "$mtlkind=blendmask";
            default -> "";
        };
    }

    private record Parm(
        String name,
        RenderParm renderParm,
        Object value
    ) {
    }

    private record Tex2D(
        String filePath,
        MaterialImageOpts options
    ) {
    }
}
