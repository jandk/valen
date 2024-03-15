package be.twofold.valen.reader.decl.material2;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.resource.*;
import com.google.gson.*;
import jakarta.inject.*;

import java.util.*;

public final class MaterialReader implements ResourceReader<Material> {
    private final ResourceManager resourceManager;
    private final DeclReader declReader;

    @Inject
    public MaterialReader(
        ResourceManager resourceManager,
        DeclReader declReader
    ) {
        this.resourceManager = resourceManager;
        this.declReader = declReader;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.RsStreamFile
               && entry.nameString().startsWith("generated/decls/material2/");
    }

    @Override
    public Material read(BetterBuffer buffer, Resource resource) {
        JsonObject object = declReader.read(buffer, resource);
        return readMaterial(object, resource.nameString());
    }


    private Material readMaterial(JsonObject object, String name) {
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

        var references = new ArrayList<TextureReference>();
        for (var entry : parms.entrySet()) {
            var type = mapTexture(entry.getKey());
            var filename = entry.getValue().getAsJsonObject()
                .get("filePath").getAsString();
            var options = entry.getValue().getAsJsonObject()
                .getAsJsonObject("options");

            if (filename.isEmpty() || options == null) {
                continue;
            }

            var requiredAttributes = new HashMap<String, String>();
            if (type == TextureType.Smoothness) {
                String normal = parms
                    .getAsJsonObject("normal")
                    .get("filePath").getAsString();
                requiredAttributes.put("smoothnessnormal", normal);
            }

            requiredAttributes.put("mtlkind", mapMtlKind(entry.getKey()));

            var optionalAttributes = new HashMap<String, String>();
            var format = mapFormat(options.get("format").getAsString());
            optionalAttributes.put(format, format);

            // var resource = resourceManager.get(filename, ResourceType.Image, requiredAttributes, optionalAttributes);
            references.add(new TextureReference(type, ""));
        }

        return new Material(materialName, references);
    }

    private String mapFormat(String format) {
        return switch (ImageTextureFormat.valueOf(format)) {
            case FMT_RGBA16F -> "float";
            case FMT_RGBA8 -> "rgba8";
            case FMT_ALPHA -> "alpha";
            case FMT_RG8 -> "rg8";
            case FMT_BC1 -> "bc1";
            case FMT_BC3 -> "bc3";
            case FMT_R8 -> "r8";
            case FMT_BC6H_UF16 -> "bc6huf16";
            case FMT_BC7 -> "bc7";
            case FMT_BC4 -> "bc4";
            case FMT_BC5 -> "bc5";
            case FMT_RG16F -> "rg16f";
            case FMT_RG32F -> "rg32f";
            case FMT_RGBA8_SRGB -> "rgba8srgb";
            case FMT_BC1_SRGB -> "bc1srgb";
            case FMT_BC3_SRGB -> "bc3srgb";
            case FMT_BC7_SRGB -> "bc7srgb";
            case FMT_BC6H_SF16 -> "bc6hsf16";
            case FMT_BC1_ZERO_ALPHA -> "bc1za";
            default -> throw new IllegalArgumentException("Unknown format: " + format);
        };
    }

    private String mapMtlKind(String name) {
        return switch (name) {
            case "bloommaskmap" -> "bloommask";
            default -> name;
        };
    }

    private TextureType mapTexture(String key) {
        return switch (key) {
            case "albedo" -> TextureType.Albedo;
            case "specular" -> TextureType.Specular;
            case "normal" -> TextureType.Normal;
            case "smoothness" -> TextureType.Smoothness;
            default -> TextureType.Unknown;
        };
    }
}
