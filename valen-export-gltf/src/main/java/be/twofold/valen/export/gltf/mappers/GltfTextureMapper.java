package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
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
    private final Map<String, TextureIDAndFactor> textures = new HashMap<>();

    private final GltfContext context;

    public GltfTextureMapper(GltfContext context) {
        this.context = context;
    }

    public TextureIDAndFactor map(TextureReference reference) throws IOException {
        var existingSchema = textures.get(reference.name());
        if (existingSchema != null) {
            return existingSchema;
        }

        var texture = reference.supplier().get();
        return map(reference.name(), texture);
    }

    public TextureIDAndFactor map(String name, Texture texture) throws IOException {
        var existingSchema = textures.get(name);
        if (existingSchema != null) {
            return existingSchema;
        }

        var scaleAndBias = scaleAndBias(texture);
        if (scaleAndBias.texture() == null) {
            return new TextureIDAndFactor(null, scaleAndBias.factor());
        }

        var buffer = textureToPng(scaleAndBias.texture());
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

        var textureIDAndFactor = new TextureIDAndFactor(textureID, scaleAndBias.factor());
        textures.put(name, textureIDAndFactor);
        return textureIDAndFactor;
    }

    private ByteBuffer textureToPng(Texture texture) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            pngExporter.export(texture, out);
            return ByteBuffer.wrap(out.toByteArray());
        }
    }

    // region Scale and Bias

    private TextureAndFactor scaleAndBias(Texture texture) {
        var scale = texture.scale();
        var bias = texture.bias();
        if (bias == 0) {
            if (scale == 1) {
                // We just didn't touch it this time
                return new TextureAndFactor(texture, Vector4.One);
            } else {
                // We can just scale the result
                var factor = new Vector4(Vector3.splat(texture.scale()), 1.0f);
                return new TextureAndFactor(texture, factor);
            }
        }
        if (scale == 0) {
            // All pixels get eaten, so only a bias
            var factor = new Vector4(Vector3.splat(texture.bias()), 1.0f);
            return new TextureAndFactor(null, factor);
        }

        // TODO: More formats
        var decoded = TextureConverter.convert(texture.firstOnly(), TextureFormat.R8G8B8A8_UNORM);
        var data = decoded.surfaces().getFirst().data();

        // Some games like to have textures for everything, even a single color...
        var single = checkAllPixelsEqual(data);
        if (single != null) {
            return new TextureAndFactor(null, single);
        }

        // Last resort: Now we have to actually scale and shit
        for (int i = 0; i < data.length; i += 4) {
            data[i/**/] = scaleAndBias(data[i/**/], scale, bias);
            data[i + 1] = scaleAndBias(data[i + 1], scale, bias);
            data[i + 2] = scaleAndBias(data[i + 2], scale, bias);
        }
        return new TextureAndFactor(decoded, Vector4.One);
    }

    private Vector4 checkAllPixelsEqual(byte[] data) {
        int pixel = ByteArrays.getInt(data, 0);
        for (int i = 0; i < data.length; i += 4) {
            if (ByteArrays.getInt(data, i) != pixel) {
                return null;
            }
        }

        // The factors are in linear space, not srgb
        return new Vector4(
            srgbToLinear((byte) (pixel)),
            srgbToLinear((byte) (pixel >> 8)),
            srgbToLinear((byte) (pixel >> 16)),
            srgbToLinear((byte) (pixel >> 24))
        );
    }

    private byte scaleAndBias(byte b, float scale, float bias) {
        return linearToSrgb(Math.fma(srgbToLinear(b), scale, bias));
    }

    private byte linearToSrgb(float value) {
        if (value <= (0.04045f / 12.92f)) {
            value *= 12.92f;
        } else {
            value = Math.fma(MathF.pow(value, 1.0f / 2.4f), 1.055f, -0.055f);
        }
        return MathF.packUNorm8(value);
    }

    private float srgbToLinear(byte b) {
        float f = MathF.unpackUNorm8(b);
        if (f <= 0.04045f) {
            return f * (1.0f / 12.92f);
        } else {
            return MathF.pow(Math.fma(f, 1.0f / 1.055f, 0.055f / 1.055f), 2.4f);
        }
    }

    private record TextureAndFactor(
        Texture texture,
        Vector4 factor
    ) {
    }

    // endregion

}
