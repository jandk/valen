package be.twofold.valen.game.eternal.reader.decl.material2;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.enums.*;
import be.twofold.valen.game.eternal.reader.image.*;
import be.twofold.valen.game.eternal.resource.*;
import com.google.gson.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class MaterialReader implements AssetReader<Material, EternalAsset> {
    private static final Map<MaterialPropertyType, List<String>> ParmTextures = Map.of(
        MaterialPropertyType.Albedo, List.of(
            "albedo",
            "decalalbedo",
            "eyealbedomap",
            "glassalbedomap",
            "hairalbedomap"
        ),
        MaterialPropertyType.Normal, List.of(
            "normal",
            "decalnormal",
            "eyenormalmap",
            "glassnormalmap",
            "hairnormalmap"
        ),
        MaterialPropertyType.Specular, List.of(
            "specular",
            "decalspecular",
            "eyespecularmap",
            "glassspecularmap",
            "hairspecularmap"
        ),
        MaterialPropertyType.Smoothness, List.of(
            "smoothness",
            "eyeouterlayersmoothnessmap",
            "eyeinnerlayersmoothnessmap",
            "glasssmoothnessmap",
            "hairsmoothnessmap"
        ),
        MaterialPropertyType.Emissive, List.of(
            "bloommaskmap",
            "eyeemissivemap"
        )
    );
    private static final Map<MaterialPropertyType, List<String>> ParmFactors = Map.of(
        MaterialPropertyType.Albedo, List.of(
            "glassalbedo"
        ),
        MaterialPropertyType.Specular, List.of(
            "hairspecular"
        ),
        MaterialPropertyType.Smoothness, List.of(
            "hairsmoothness"
        ),
        MaterialPropertyType.Emissive, List.of(
            "surfaceemissivecolor"
        )
    );

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
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.RsStreamFile
            && resource.id().name().name().startsWith("generated/decls/material2/");
    }

    @Override
    public Material read(DataSource source, EternalAsset resource) throws IOException {
        var object = declReader.read(source, resource);
        return parseMaterial(object, resource.id().fullName());
    }

    public static final Set<String> RenderProgs = new HashSet<>();
    public static final Map<String, Set<String>> RenderParms = new HashMap<>();

    private Material parseMaterial(JsonObject object, String name) throws IOException {
        var materialName = name
            .replace("generated/decls/material2/", "")
            .replace(".decl", "");

        JsonObject passes = object.getAsJsonObject("Passes");
        Optional.ofNullable(passes)
            .map(o -> o.getAsJsonObject("MATERIALPASS_OPAQUE"))
            .map(o -> o.get("prog"))
            .map(JsonElement::getAsString)
            .ifPresent(RenderProgs::add);

        if (!object.has("RenderLayers") && !object.has("Parms")) {
            return new Material(materialName, List.of());
        }

        var renderLayerParms = parseRenderLayerParms(object.getAsJsonArray("RenderLayers"));
        var standardParms = parseParms(object.getAsJsonObject("Parms"));

        var allParms = Stream.of(renderLayerParms, standardParms)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableMap(Parm::name, r -> r));

        if (passes != null) {
            passes.entrySet().stream()
                .filter(e -> !e.getValue().isJsonNull())
                .map(e -> e.getValue().getAsJsonObject().get("prog"))
                .filter(e -> e != null && !e.isJsonNull())
                .forEach(e -> RenderParms
                    .computeIfAbsent(e.getAsString(), _ -> new HashSet<>())
                    .addAll(allParms.keySet()));
        }

        var albedo = mapSimpleTexture(allParms, MaterialPropertyType.Albedo);
        var normal = mapSimpleTexture(allParms, MaterialPropertyType.Normal);
        var specular = mapSimpleTexture(allParms, MaterialPropertyType.Specular);
        var smoothness = mapSimpleTexture(allParms, MaterialPropertyType.Smoothness);
        var emissive = mapEmissive(allParms);

        var other = allParms.values().stream()
            .filter(parm -> parm.value instanceof Tex)
            .map(parm -> mapTex2D(parm, allParms))
            .filter(Objects::nonNull)
            .map(reference -> new MaterialProperty(MaterialPropertyType.Unknown, reference, null));

        var properties = Stream.concat(Stream.of(albedo, normal, specular, smoothness, emissive), other)
            .filter(Objects::nonNull)
            .toList();

        return new Material(materialName, properties);
    }

    private MaterialProperty mapEmissive(Map<String, Parm> allParms) {
        var emissive = mapSimpleTexture(allParms, MaterialPropertyType.Emissive);
        var emissiveColor = allParms.get("surfaceemissivecolor") != null ? (Vector3) allParms.get("surfaceemissivecolor").value() : Vector3.One;
        var emissiveScale = allParms.get("surfaceemissivescale") != null ? (Float) allParms.get("surfaceemissivescale").value() : 1.0f;
        var emissiveFactor = new Vector4(emissiveColor, emissiveScale);

        if (emissive == null) {
            return new MaterialProperty(MaterialPropertyType.Specular, null, emissiveFactor);
        }
        return emissive.withFactor(emissiveFactor);
    }

    private MaterialProperty mapSimpleTexture(Map<String, Parm> parms, MaterialPropertyType propertyType) {
        var textureParm = ParmTextures.getOrDefault(propertyType, List.of()).stream()
            .map(parms::get)
            .filter(Objects::nonNull)
            .findFirst();

        var factorParm = ParmFactors.getOrDefault(propertyType, List.of()).stream()
            .map(parms::get)
            .filter(Objects::nonNull)
            .findFirst();

        if (textureParm.isEmpty() && factorParm.isEmpty()) {
            return null;
        }

        var reference = textureParm.map(parm -> mapTex2D(parm, parms)).orElse(null);
        var factor = factorParm.map(parm -> switch (parm.renderParm().parmType) {
            case PT_F32_VEC4 -> (Vector4) parm.value();
            case PT_F32_VEC3 -> new Vector4((Vector3) parm.value(), 0.0f);
            case PT_F32_VEC2 -> new Vector4((Vector2) parm.value(), 0.0f, 0.0f);
            case PT_F32 -> new Vector4(0.0f, 0.0f, 0.0f, (Float) parm.value());
            default -> throw new UnsupportedOperationException();
        }).orElse(null);

        if (reference == null && factor == null) {
            return null;
        }

        return new MaterialProperty(propertyType, reference, factor);
    }

    private TextureReference mapTex2D(Parm parm, Map<String, Parm> allParms) {
        var filePath = ((Tex) parm.value).filePath();
        if (filePath == null) {
            return null;
        }

        var builder = new StringBuilder(filePath);
        if (parm.renderParm().materialKind == ImageTextureMaterialKind.TMK_SMOOTHNESS) {
            var smoothnessNormal = allParms.entrySet().stream()
                .filter(rlp -> rlp.getValue().renderParm().materialKind == parm.renderParm().smoothnessNormalParm)
                .findFirst().orElseThrow();

            builder
                .append("$smoothnessnormal=")
                .append(((Tex) smoothnessNormal.getValue().value()).filePath());
        }
        mapOptions(builder, parm);

        var name = builder.toString();
        var resourceName = new ResourceName(name);
        var resourceKey = EternalAssetID.from(resourceName, ResourceType.Image);
        var resource = archive.get(resourceKey);
        if (resource.isEmpty()) {
            log.warn("Missing image file: {}", name);
            return null;
        }

        var supplier = ThrowingSupplier.lazy(() -> archive.loadAsset(resourceKey, Texture.class));
        return new TextureReference(name, resource.get().exportName(), supplier);
    }

    private void mapOptions(StringBuilder builder, Parm parm) {
        var tex2d = (Tex) parm.value();
        var opts = tex2d.options();
        var kind = parm.renderParm().materialKind;
        if (opts != null) {
            if (opts.format() != ImageTextureFormat.FMT_NONE) {
                if (kind == null || (kind.getCode() > 7 && kind != ImageTextureMaterialKind.TMK_BLENDMASK)) {
                    builder.append(formatFormat(opts.format()));
                }
            }
            if (opts.atlasPadding() != null && opts.atlasPadding() != 0) {
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
            if (kind != null) {
                builder.append(formatMaterialKind(kind));
            }
        }
    }

    private List<Parm> parseRenderLayerParms(JsonArray array) throws IOException {
        if (array == null) {
            return List.of();
        }
        var parms = array
            .get(0).getAsJsonObject()
            .getAsJsonObject("parms");

        var renderLayerParms = new ArrayList<Parm>();
        for (var entry : parms.entrySet()) {
            var renderParm = getRenderParm(entry.getKey());
            if (renderParm.isEmpty()) {
                log.warn("Skipping unknown render layer parm: {}", entry.getKey());
                continue;
            }

            var tex2d = parseTex(entry.getValue());
            renderLayerParms.add(new Parm(entry.getKey(), renderParm.get(), tex2d));
        }
        return renderLayerParms;
    }

    private List<Parm> parseParms(JsonObject object) throws IOException {
        var result = new ArrayList<Parm>();
        for (var entry : object.entrySet()) {
            var parm = parseParm(object, entry.getKey());
            parm.ifPresent(result::add);
        }
        return result;
    }

    private Optional<Parm> parseParm(JsonObject parms, String name) throws IOException {
        if (!parms.has(name)) {
            return Optional.empty();
        }

        var renderParm = getRenderParm(name);
        if (renderParm.isEmpty()) {
            return Optional.empty();
        }

        var element = parms.get(name);
        var value = switch (renderParm.get().parmType) {
            case PT_BOOL -> {
                if (element.isJsonObject()) {
                    // TODO: Should we look at X or W?
                    var object = element.getAsJsonObject();
                    if (!object.has("w")) {
                        yield renderParm.get().declaredValue;
                    }
                    yield object.getAsJsonPrimitive("w").getAsInt() != 0;
                }
                yield element.getAsString();
            }
            case PT_F32 -> {
                if (element.isJsonObject()) {
                    var object = element.getAsJsonObject();
                    if (!object.has("x")) {
                        yield renderParm.get().declaredValue;
                    }
                    yield object.get("x").getAsFloat();
                }
                yield element.getAsFloat();
            }
            case PT_F32_VEC2 -> parseVector2(element, renderParm.get());
            case PT_F32_VEC3 -> parseVector3(element, renderParm.get());
            case PT_F32_VEC4 -> parseVector4(element, renderParm.get());
            case PT_STRING -> element.getAsString();
            case PT_TEXTURE_2D, PT_TEXTURE_CUBE -> parseTex(element);
            case PT_UI32 -> element.getAsInt();
            default -> throw new UnsupportedOperationException(renderParm.get().parmType.toString());
        };

        return Optional.of(new Parm(name, renderParm.get(), value));
    }

    private Vector2 parseVector2(JsonElement element, RenderParm renderParm) {
        var object = element.getAsJsonObject();
        var defaultValue = (Vector2) renderParm.declaredValue;
        var x = object.has("x") ? object.getAsJsonPrimitive("x").getAsFloat() : defaultValue.x();
        var y = object.has("y") ? object.getAsJsonPrimitive("y").getAsFloat() : defaultValue.y();
        var result = new Vector2(x, y);
        return renderParm.parmEdit instanceof ParmEdit.Srgba
            ? result.map(MathF::srgbToLinear)
            : result;
    }

    private Vector3 parseVector3(JsonElement element, RenderParm renderParm) {
        var object = element.getAsJsonObject();
        var defaultValue = (Vector3) renderParm.declaredValue;
        var x = object.has("x") ? object.getAsJsonPrimitive("x").getAsFloat() : defaultValue.x();
        var y = object.has("y") ? object.getAsJsonPrimitive("y").getAsFloat() : defaultValue.y();
        var z = object.has("z") ? object.getAsJsonPrimitive("z").getAsFloat() : defaultValue.z();
        var result = new Vector3(x, y, z);
        return renderParm.parmEdit instanceof ParmEdit.Srgba
            ? result.map(MathF::srgbToLinear)
            : result;
    }

    private Vector4 parseVector4(JsonElement element, RenderParm renderParm) {
        var object = element.getAsJsonObject();
        var defaultValue = (Vector4) renderParm.declaredValue;
        var x = object.has("x") ? object.getAsJsonPrimitive("x").getAsFloat() : defaultValue.x();
        var y = object.has("y") ? object.getAsJsonPrimitive("y").getAsFloat() : defaultValue.y();
        var z = object.has("z") ? object.getAsJsonPrimitive("z").getAsFloat() : defaultValue.z();
        var w = object.has("w") ? object.getAsJsonPrimitive("w").getAsFloat() : defaultValue.w();
        var result = new Vector4(x, y, z, w);
        return renderParm.parmEdit instanceof ParmEdit.Srgba
            ? result.map(MathF::srgbToLinear)
            : result;
    }

    private Optional<RenderParm> getRenderParm(String name) throws IOException {
        if (RenderParmCache.containsKey(name)) {
            return Optional.ofNullable(RenderParmCache.get(name));
        }

        var fullName = "generated/decls/renderparm/" + name + ".decl";
        var resourceKey = EternalAssetID.from(fullName, ResourceType.RsStreamFile);
        if (!archive.exists(resourceKey)) {
            RenderParmCache.put(fullName, null);
            return Optional.empty();
        }

        var renderParm = archive.loadAsset(resourceKey, RenderParm.class);
        RenderParmCache.put(name, renderParm);
        return Optional.of(renderParm);
    }

    private Tex parseTex(JsonElement element) {
        var object = element.getAsJsonObject();
        var filePath = object.has("filePath") ? object.get("filePath").getAsString() : null;
        var options = parseOptions(object.getAsJsonObject("options"));

        return new Tex(filePath, options);
    }

    private MaterialImageOpts parseOptions(JsonObject options) {
        if (options == null) {
            return null;
        }

        var type = options.has("type") ? ImageTextureType.valueOf(options.get("type").getAsString()) : null;
        var filter = options.has("filter") ? ImageTextureFilter.valueOf(options.get("filter").getAsString()) : null;
        var repeat = options.has("repeat") ? ImageTextureRepeat.valueOf(options.get("repeat").getAsString()) : null;
        var format = options.has("format") ? ImageTextureFormat.valueOf(options.get("format").getAsString()) : null;
        var atlasPadding = options.has("atlasPadding") ? options.get("atlasPadding").getAsShort() : null;
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

    private record Tex(
        String filePath,
        MaterialImageOpts options
    ) {
    }
}
