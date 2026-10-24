package be.twofold.valen.game.idtech.reader.decl.material2;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.reader.decl.*;
import be.twofold.valen.game.idtech.reader.decl.renderparm.*;
import be.twofold.valen.game.idtech.reader.image.*;
import be.twofold.valen.game.idtech.reader.image.TextureFormat;
import com.google.gson.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class AbstractMaterialReader<K extends IdclAssetID<?, ?>, V extends IdclAsset<K>> implements AssetReader.Binary<Material, V> {
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
            // "eyeouterlayersmoothnessmap", // these are RGB in TDA WTF?
            // "eyeinnerlayersmoothnessmap", // these are RGB in TDA WTF?
            "glasssmoothnessmap",
            "hairsmoothnessmap"
        ),
        MaterialPropertyType.Emissive, List.of(
            "bloommaskmap",
            "eyeemissivemap"
        ),
        MaterialPropertyType.Occlusion, List.of(
            "aomap"
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
        ),
        MaterialPropertyType.Occlusion, List.of(
            "aointensitypower"
        )
    );

    private final Map<String, RenderParm> renderParmCache = Collections.synchronizedMap(new HashMap<>());
    private final Logger log;
    private final IdTechGame game;
    private final AbstractDeclReader<K, V> declReader;

    protected AbstractMaterialReader(AbstractDeclReader<K, V> declReader, IdTechGame game) {
        this.log = LoggerFactory.getLogger(getClass());
        this.declReader = Check.nonNull(declReader, "declReader");
        this.game = Check.nonNull(game, "game");
    }

    @Override
    public abstract boolean canRead(V asset);

    @Override
    public final Material read(BinarySource source, V asset, LoadingContext context) throws IOException {
        var object = declReader.read(source, asset, context);
        return parseMaterial(object, asset, context);
    }

    public abstract String materialName(V asset);

    public abstract K imageAssetID(String name);

    public abstract K renderParmAssetID(String name);

    protected Material parseMaterial(JsonObject object, V asset, LoadingContext context) throws IOException {
        var materialName = materialName(asset);
        if (!object.has("RenderLayers") && !object.has("Parms")) {
            return new Material(materialName, List.of());
        }

        var renderLayerParms = parseRenderLayerParms(object.getAsJsonArray("RenderLayers"), context);
        var standardParms = parseParms(object.getAsJsonObject("Parms"), context);

        var allParms = Stream.of(renderLayerParms, standardParms)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableMap(MaterialParm::name, Function.identity(), (_, second) -> second));

        var albedo = mapSimpleTexture(allParms, MaterialPropertyType.Albedo, context);
        var normal = mapSimpleTexture(allParms, MaterialPropertyType.Normal, context);
        var specular = mapSimpleTexture(allParms, MaterialPropertyType.Specular, context);
        var smoothness = mapSimpleTexture(allParms, MaterialPropertyType.Smoothness, context);
        var occlusion = mapSimpleTexture(allParms, MaterialPropertyType.Occlusion, context);
        var emissive = mapEmissive(allParms, context);

        var other = allParms.values().stream()
            .filter(parm -> parm.value() instanceof MaterialTex)
            .map(parm -> mapTex2D(parm, allParms, context))
            .filter(Objects::nonNull)
            .map(reference -> new MaterialProperty(MaterialPropertyType.Unknown, reference, null));

        var properties = Stream.concat(Stream.of(albedo, normal, specular, smoothness, occlusion, emissive), other)
            .filter(Objects::nonNull)
            .toList();

        return new Material(materialName, properties);
    }

    private MaterialProperty mapEmissive(Map<String, MaterialParm> allParms, LoadingContext context) {
        var emissive = mapSimpleTexture(allParms, MaterialPropertyType.Emissive, context);
        if (emissive == null) {
            return null;
        }

        var emissiveColor = allParms.get("surfaceemissivecolor") != null ? (Vector3) allParms.get("surfaceemissivecolor").value() : Vector3.ONE;
        var emissiveScale = allParms.get("surfaceemissivescale") != null ? (Float) allParms.get("surfaceemissivescale").value() : 1.0f;
        var emissiveFactor = new Vector4(emissiveColor, emissiveScale);
        return emissive.withFactor(emissiveFactor);
    }

    private MaterialProperty mapSimpleTexture(Map<String, MaterialParm> parms, MaterialPropertyType propertyType, LoadingContext context) {
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

        var reference = textureParm.map(parm -> mapTex2D(parm, parms, context)).orElse(null);
        var factor = factorParm.map(parm -> switch (parm.renderParm().parmType()) {
            case PT_F32_VEC4, PT_F16_VEC4 -> (Vector4) parm.value();
            case PT_F32_VEC3, PT_F16_VEC3 -> new Vector4((Vector3) parm.value(), 0.0f);
            case PT_F32_VEC2, PT_F16_VEC2 -> new Vector4((Vector2) parm.value(), 0.0f, 0.0f);
            case PT_F32, PT_F16 -> new Vector4(0.0f, 0.0f, 0.0f, (Float) parm.value());
            default -> throw new UnsupportedOperationException();
        }).orElse(null);

        if (reference == null && factor == null) {
            return null;
        }

        return new MaterialProperty(propertyType, reference, factor);
    }

    private TextureReference mapTex2D(MaterialParm parm, Map<String, MaterialParm> allParms, LoadingContext context) {
        var filePath = ((MaterialTex) parm.value()).filePath();
        if (filePath == null || filePath.isEmpty() || filePath.startsWith("_")) {
            return null; // Runtime images
        }

        var assetPath = AssetPath.of(filePath);
        if (game == IdTechGame.DARK_AGES && assetPath.extension().isEmpty()) {
            assetPath = assetPath.withExtension("tga");
        }

        var builder = new StringBuilder(assetPath.toString().toLowerCase(Locale.ROOT));
        var smoothnessNormalParm = parm.renderParm().smoothnessNormalParm();
        var materialKind = parm.renderParm().materialKind();

        if (game != IdTechGame.DARK_AGES && materialKind != null && materialKind.isSmoothness() && smoothnessNormalParm != null) {
            builder
                .append("$smoothnessnormal=")
                .append(smoothnessNormalPath(smoothnessNormalParm, allParms, context));
        }
        mapOptions(builder, parm);

        var name = builder.toString();
        var imageAssetID = imageAssetID(name);
        if (!context.exists(imageAssetID)) {
            log.warn("Missing image file: '{}'", name);
            return null;
        }

        var supplier = ThrowingSupplier.lazy(() -> context.load(imageAssetID, Texture.class));
        return new TextureReference(name, imageAssetID.hashedBaseName(), supplier);
    }

    private String smoothnessNormalPath(String smoothnessNormalParm, Map<String, MaterialParm> allParms, LoadingContext context) {
        for (var entry : allParms.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(smoothnessNormalParm)
                && entry.getValue().value() instanceof MaterialTex tex
                && tex.filePath() != null
                && !tex.filePath().isEmpty()
            ) {
                return tex.filePath();
            }
        }
        return defaultSmoothnessNormal(smoothnessNormalParm, context);
    }

    private String defaultSmoothnessNormal(String smoothnessNormalParm, LoadingContext context) {
        Optional<RenderParm> renderParm;
        try {
            renderParm = getRenderParm(smoothnessNormalParm, context);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (renderParm.isEmpty() || !(renderParm.get().declaredValue() instanceof ImageProperties image)) {
            return "";
        }
        var index = image.name().indexOf('$');
        return index >= 0
            ? image.name().substring(0, index)
            : image.name();
    }

    private List<MaterialParm> parseRenderLayerParms(JsonArray array, LoadingContext context) throws IOException {
        if (array == null) {
            return List.of();
        }
        var parms = array
            .get(0).getAsJsonObject()
            .getAsJsonObject("parms");

        if (parms == null) {
            return List.of();
        }
        var renderLayerParms = new ArrayList<MaterialParm>();
        for (var entry : parms.entrySet()) {
            var renderParm = getRenderParm(entry.getKey(), context);
            if (renderParm.isEmpty()) {
                log.warn("Skipping unknown render layer parm: {}", entry.getKey());
                continue;
            }

            var tex2d = MaterialTex.parse(entry.getValue(), renderParm.get());
            renderLayerParms.add(new MaterialParm(entry.getKey(), renderParm.get(), tex2d));
        }
        return renderLayerParms;
    }

    private List<MaterialParm> parseParms(JsonObject object, LoadingContext context) throws IOException {
        if (object == null) {
            return List.of();
        }

        var result = new ArrayList<MaterialParm>();
        for (var entry : object.entrySet()) {
            getRenderParm(entry.getKey(), context)
                .ifPresent(parm -> result.add(MaterialParm.parse(entry.getKey(), entry.getValue(), parm)));
        }
        return result;
    }

    private Optional<RenderParm> getRenderParm(String name, LoadingContext context) throws IOException {
        name = name.toLowerCase(Locale.ROOT);
        if (renderParmCache.containsKey(name)) {
            return Optional.ofNullable(renderParmCache.get(name));
        }

        var renderParmAsset = renderParmAssetID(name);
        if (!context.exists(renderParmAsset)) {
            log.warn("Could not load renderparm: '{}'", name);
            renderParmCache.put(name, null);
            return Optional.empty();
        }

        var renderParm = context.load(renderParmAsset, RenderParm.class);
        renderParmCache.put(name, renderParm);
        return Optional.of(renderParm);
    }

    private void mapOptions(StringBuilder builder, MaterialParm parm) {
        var tex2d = (MaterialTex) parm.value();
        var opts = tex2d.options();
        var kind = parm.renderParm().materialKind();

        addTextureFormat(builder, kind, opts.format());
        if (opts.atlasPadding() != 0) {
            builder.append(formatAtlasPadding(opts.atlasPadding()));
        }
        if (game != IdTechGame.DARK_AGES && opts.minMip() != 0) {
            builder.append("$minmip=").append(opts.minMip());
        }
        if (opts.fullScaleBias()) {
            builder.append("$fullscalebias");
        }
        if (parm.renderParm().flags().contains(RenderParm.Flags.STREAMED)) {
            builder.append("$streamed");
        }
        if (opts.noMips()) {
            builder.append("$nomips");
        }
        if (opts.type() == TextureType.TT_3D) {
            builder.append(game == IdTechGame.DARK_AGES
                ? "$volume=" + opts.width3D() + "x" + opts.height3D() + "x" + opts.depth3D()
                : "$volume=0");
        }
        if (game != IdTechGame.DARK_AGES && opts.fftBloom()) {
            builder.append("$fftbloom");
        }
        if (game == IdTechGame.DARK_AGES && opts.prefilterMips()) {
            builder.append("$prefiltermips");
        }
        if (kind != null && kind.suffix() != null) {
            builder.append("$mtlkind=").append(kind.suffix());
        }
    }

    private void addTextureFormat(StringBuilder builder, MaterialKind kind, TextureFormat format) {
        if (format == null || format == TextureFormat.FMT_NONE) {
            return;
        }
        if (kind != null && kind.skipsFormat(format)) {
            return;
        }
        builder.append(formatTextureFormat(format));
    }

    private String formatTextureFormat(TextureFormat format) {
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

    private String formatAtlasPadding(int atlasPadding) {
        return switch (atlasPadding) {
            case 1, 2 -> "$pad2";
            case 3, 4 -> "$pad4";
            case 5, 6, 7, 8 -> "$pad8";
            case 9, 10, 11, 12, 13, 14, 15, 16 -> "$pad16";
            default -> throw new IllegalArgumentException("Unknown atlas padding: " + atlasPadding);
        };
    }
}
