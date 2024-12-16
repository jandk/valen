package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.png.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.image.*;
import be.twofold.valen.gltf.model.texture.*;
import org.slf4j.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class GltfTextureMapper {
    private static final Logger log = LoggerFactory.getLogger(GltfTextureMapper.class);
    private final PngExporter pngExporter = new PngExporter();
    private final Map<String, TextureID> textures = new HashMap<>();

    private final GltfContext context;

    public GltfTextureMapper(GltfContext context) {
        this.context = context;
    }

    public TextureID map(TextureReference reference) throws IOException {
        var existingSchema = textures.get(reference.name());
        if (existingSchema != null) {
            return existingSchema;
        }

        var texture = reference.supplier().get();
        return map(reference.name(), texture);
    }

    public TextureID map(String name, Texture texture) throws IOException {
        var existingSchema = textures.get(name);
        if (existingSchema != null) {
            return existingSchema;
        }

        if (texture.scale() != 1 || texture.bias() != 0) {
            log.warn("scaleAndBias '{}' with {} and {}", name, texture.scale(), texture.bias());
            texture = scaleAndBias(texture);
        }

        var buffer = textureToPng(texture);
        var bufferViewID = context.createBufferView(buffer);

        var imageSchema = ImageSchema.builder()
            .name(name)
            .mimeType(ImageMimeType.IMAGE_PNG)
            .bufferView(bufferViewID)
            .build();
        var imageID = context.addImage(imageSchema);

        var textureSchema = TextureSchema.builder()
            .name(name)
            .source(imageID)
            .build();
        var textureID = context.addTexture(textureSchema);

        textures.put(name, textureID);

        return textureID;
    }

    private Texture scaleAndBias(Texture texture) {
        // TODO: Fix this to support more formats
        var decoded = TextureConverter.convert(texture.firstOnly(), TextureFormat.R8G8B8A8_UNORM);

        var scale = texture.scale();
        var bias = texture.bias();
        var data = decoded.surfaces().getFirst().data();
        for (int i = 0; i < data.length; i += 4) {
            data[i/**/] = scaleAndBias(data[i/**/], scale, bias);
            data[i + 1] = scaleAndBias(data[i + 1], scale, bias);
            data[i + 2] = scaleAndBias(data[i + 2], scale, bias);
        }

        return decoded;
    }

    private byte scaleAndBias(byte b, float scale, float bias) {
        var value = srgbToLinear(MathF.unpackUNorm8(b));
        value = Math.fma(value, scale, bias);
        return MathF.packUNorm8(linearToSrgb(value));
    }

    private float linearToSrgb(float value) {
        if (value <= 0.0031308f) {
            return value * 12.92f;
        } else {
            return Math.fma(MathF.pow(value, 1.0f / 2.4f), 1.055f, -0.055f);
        }
    }

    private float srgbToLinear(float value) {
        if (value <= 0.04045f) {
            return value * (1.0f / 12.92f);
        } else {
            return MathF.pow(Math.fma(value, 1.0f / 1.055f, 0.055f / 1.055f), 2.4f);
        }
    }

    private ByteBuffer textureToPng(Texture texture) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            pngExporter.export(texture, out);
            return ByteBuffer.wrap(out.toByteArray());
        }
    }
}
