package be.twofold.valen.game.doom.readers.material;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.shader.*;
import be.twofold.valen.core.texture.shader.node.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.doom.vmtr.*;
import be.twofold.valen.game.idtech.game.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

final class MaterialBuilder {
    private static final Logger log = LoggerFactory.getLogger(MaterialBuilder.class);

    private static final Map<String, MaterialPropertyType> TYPES = Map.of(
        "bumpmap", MaterialPropertyType.Normal,
        "diffusemap", MaterialPropertyType.Albedo,
        "powermap", MaterialPropertyType.Emissive,
        "specularmap", MaterialPropertyType.Specular
    );

    private static final Set<String> VMTR_MAPS = Set.of(
        "bloommask",
        "bumpmap",
        "colormaskmap",
        "covermap",
        "diffusemap",
        "powermap",
        "specularmap",
        "sssmap",
        "virtualtexturemap"
    );

    private static final Set<String> TOOL_MAPS = Set.of(
        "editormap",
        "qer_editorimage",
        "renderbump"
    );

    private final String name;
    private final AssetPath path;
    private final Map<String, String> parms;
    private final LoadingContext context;
    private final List<MaterialProperty> properties = new ArrayList<>();
    private final Map<VmtrLayer, ThrowingSupplier<Texture, IOException>> layers = new EnumMap<>(VmtrLayer.class);

    MaterialBuilder(String name, Map<String, String> parms, LoadingContext context) {
        this.name = name;
        this.path = AssetPath.of(name);
        this.parms = parms;
        this.context = context;
    }

    Material build() {
        var virtual = isEnabled("usevirtualmapping");
        if (virtual) {
            addVirtual();
        }
        for (var entry : parms.entrySet()) {
            var key = entry.getKey();
            if ((virtual && VMTR_MAPS.contains(key)) || TOOL_MAPS.contains(key)) {
                continue;
            }
            var value = entry.getValue();
            if (!TYPES.containsKey(key) && !value.toLowerCase(Locale.ROOT).contains(".tga")) {
                continue;
            }
            addImage(TYPES.getOrDefault(key, MaterialPropertyType.Unknown), value);
        }
        return new Material(name, properties);
    }

    private void addVirtual() {
        if (!context.exists(new DoomAssetID(name, VmtrLayer.DIFFUSE))) {
            return;
        }

        // Albedo
        addVmtr(MaterialPropertyType.Albedo, VmtrLayer.DIFFUSE, textureName("diffusemap", ""), MaterialBuilder::clearAlpha);

        // Specular
        if (isEnabled("packedspec")) {
            addVmtr(MaterialPropertyType.Specular, VmtrLayer.SPECULAR, textureName("specularmap", "_s"),
                node -> clearAlpha(ShaderNode.splat(node, Channel.GREEN, Channel.RED, Channel.BLUE)));
            addVmtrRaw(VmtrLayer.SPECULAR);
        } else {
            addVmtr(MaterialPropertyType.Specular, VmtrLayer.SPECULAR, textureName("specularmap", "_s"), MaterialBuilder::clearAlpha);
        }

        // Normal
        var diffuse = layer(VmtrLayer.DIFFUSE);
        var specular = layer(VmtrLayer.SPECULAR);
        add(MaterialPropertyType.Normal, textureName("bumpmap", "_local"), () -> combineNormal(diffuse.get(), specular.get()));

        // Smoothness
        var masks = layer(VmtrLayer.LIGHTMAP);
        add(MaterialPropertyType.Smoothness, path.withSuffix("_smoothness"), () -> extractSmoothness(masks.get()));

        // Emissive
        if (parms.containsKey("powermap")) {
            addVmtr(MaterialPropertyType.Emissive, VmtrLayer.LIGHTMAP, textureName("powermap", "_pm"),
                node -> clearAlpha(ShaderNode.splat(node, Channel.RED, Channel.GREEN, Channel.BLUE)));
        }

        // Unknowns
        addVmtrRaw(VmtrLayer.LIGHTMAP);
        if (parms.containsKey("colormaskmap")) {
            addVmtrRaw(VmtrLayer.COLORMASK);
        }
        if (parms.containsKey("covermap")) {
            addVmtrRaw(VmtrLayer.COVER);
        }
    }

    private void addVmtr(MaterialPropertyType type, VmtrLayer layer, AssetPath textureName, UnaryOperator<ShaderNode> decorator) {
        add(type, textureName, () -> decorate(layer(layer).get(), decorator));
    }

    private void addVmtrRaw(VmtrLayer layer) {
        addVmtr(MaterialPropertyType.Unknown, layer, path.withSuffix("_vmtr" + layer.suffix()), UnaryOperator.identity());
    }

    private void addImage(MaterialPropertyType type, String value) {
        var path = imagePath(value);
        if (path.isEmpty() || path.startsWith("_")) {
            return; // Special cases
        }
        var id = new DoomAssetID(imageName(value), ResourceType.of("image"));
        if (!context.exists(id)) {
            log.debug("Missing image '{}' in material '{}'", id.name(), name);
            return;
        }

        // DOOM (2016) uses DirectX normal maps, so invert Y
        UnaryOperator<ShaderNode> decorator = (type == MaterialPropertyType.Normal)
            ? node -> ShaderNode.invert(node, Channel.GREEN)
            : UnaryOperator.identity();

        add(type, id.name(), id.hashedBaseName(), () -> decorate(context.load(id, Texture.class), decorator));
    }

    private void add(MaterialPropertyType type, AssetPath textureName, ThrowingSupplier<Texture, IOException> supplier) {
        add(type, textureName.value(), textureName.fileName(), supplier);
    }

    private void add(MaterialPropertyType type, String textureName, String fileName, ThrowingSupplier<Texture, IOException> supplier) {
        var reference = new TextureReference(textureName, fileName, ThrowingSupplier.lazy(supplier));
        properties.add(new MaterialProperty(type, reference, null));
    }

    // Load each layer only once, as it's expensive
    private ThrowingSupplier<Texture, IOException> layer(VmtrLayer layer) {
        return layers.computeIfAbsent(layer, l -> ThrowingSupplier.lazy(
            () -> context.load(new DoomAssetID(name, l), Texture.class)));
    }

    // The (thank God), way simpler version of what idImage::NameWithOpts does in newer games
    static String imageName(String value) {
        var words = value.toLowerCase(Locale.ROOT).split("\\s+");
        var path = words[words.length - 1];
        if (path.startsWith("_") || path.endsWith(".bimage")) {
            return path;
        }

        String filter = "";
        String repeat = "";
        String format = "";
        String subFormat = "";
        for (var i = 0; i < words.length - 1; i++) {
            switch (words[i]) {
                case "linear", "nearest" -> {
                    filter = "$" + words[i];
                }
                case "borderclamp", "clamp", "clamps", "clampt", "mirror" -> {
                    repeat = "$" + words[i];
                }
                case "alpha", "bc4", "bc6h", "bc7", "float", "intensity", "luminancealpha", "uncompressed" -> {
                    format = "$" + words[i];
                }
                case "hqcompress", "hqcompressnormal", "ycocgdxt5" -> {
                    subFormat = "$" + words[i];
                }
            }
        }
        return path + filter + repeat + (subFormat.isEmpty() ? format : subFormat);
    }

    private boolean isEnabled(String key) {
        var value = parms.get(key);
        return (value != null) && (Float.parseFloat(value) != 0.0f);
    }

    // The name of the source image
    private AssetPath textureName(String key, String fallbackSuffix) {
        var value = parms.get(key);
        if ((value == null) || value.isEmpty()) {
            return path.withSuffix(fallbackSuffix);
        }
        return AssetPath.of(imagePath(value)).withoutExtension();
    }

    // There's random options before the path, so yeet those
    private static String imagePath(String value) {
        return List.of(value.split("\\s+")).getLast();
    }

    private static ShaderNode clearAlpha(ShaderNode node) {
        return ShaderNode.fill(node, 1.0f, Channel.ALPHA);
    }

    private static Texture decorate(Texture texture, UnaryOperator<ShaderNode> decorator) {
        UnaryOperator<ShaderNode> combined = node -> decorator.apply(texture.decorator().apply(node));
        return new Texture(texture.format(), texture.kind(), texture.width(), texture.height(), texture.depthOrLayers(), texture.surfaces(), combined);
    }

    // Normal is X: diffuse.A, Y: specular.A
    private static Texture combineNormal(Texture diffuse, Texture specular) {
        var x = ShaderNode.source();
        var y = ShaderNode.source();
        var merged = ShaderNode.merge(builder -> builder
            .channel(Channel.ALPHA, x, Channel.RED)
            .channel(Channel.ALPHA, y, Channel.GREEN));

        // DOOM (2016) uses DirectX normal maps, so invert Y
        var inverted = ShaderNode.invert(merged, Channel.GREEN);
        var shader = Shader.of(inverted, TextureFormat.R8G8_UNORM);

        var surface = shader.execute(
            x.bind(diffuse.surfaces().getFirst()),
            y.bind(specular.surfaces().getFirst())
        );
        return Texture.fromSurface(surface);
    }

    // Smoothness is mask.A, but srgb. So a bit of a hack
    private static Texture extractSmoothness(Texture masks) {
        var source = ShaderNode.source();
        var shader = Shader.of(ShaderNode.splat(source, Channel.ALPHA, Channel.RED), TextureFormat.R8_UNORM);

        var surface = shader.execute(source.bind(masks.surfaces().getFirst()));
        return Texture.fromSurface(new Surface(
            TextureFormat.R8_SRGB,
            surface.width(),
            surface.height(),
            surface.depth(),
            surface.data()
        ));
    }
}
