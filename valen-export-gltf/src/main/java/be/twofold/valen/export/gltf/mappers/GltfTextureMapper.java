package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.png.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.image.*;
import be.twofold.valen.format.gltf.model.texture.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

public final class GltfTextureMapper {
    private final PngExporter pngExporter = new PngExporter();
    private final Map<String, TextureIDAndFactor> textures = new ConcurrentHashMap<>();

    private final GltfContext context;

    public GltfTextureMapper(GltfContext context) {
        this.context = context;
        pngExporter.setProperty("reconstructZ", true);
    }

    public TextureIDAndFactor map(TextureReference reference) throws IOException {
        var existingSchema = textures.get(reference.filename());
        if (existingSchema != null) {
            return existingSchema;
        }

        var texture = reference.supplier().get();
        var scaleAndBias = scaleAndBias(texture);
        if (scaleAndBias.texture() == null) {
            return new TextureIDAndFactor(null, scaleAndBias.factor());
        }

        return map(reference, scaleAndBias.texture(), scaleAndBias.factor());
    }

    public TextureID mapSimple(TextureReference reference) throws IOException {
        var existingSchema = textures.get(reference.filename());
        if (existingSchema != null) {
            return existingSchema.textureID();
        }

        var texture = reference.supplier().get();
        return map(reference, texture, Vector4.One).textureID();
    }

    private TextureIDAndFactor map(TextureReference reference, Texture texture, Vector4 factor) throws IOException {
        var existingSchema = textures.get(reference.filename());
        if (existingSchema != null) {
            return existingSchema;
        }

        var imageID = context.createImage(
            textureToPng(texture),
            reference.name(),
            reference.filename(),
            ImageMimeType.IMAGE_PNG
        );

        var textureSchema = ImmutableTexture.builder()
            .name(reference.name())
            .source(imageID)
            .build();
        var textureID = context.addTexture(textureSchema);

        var textureIDAndFactor = new TextureIDAndFactor(textureID, factor);
        textures.put(reference.filename(), textureIDAndFactor);
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

        var format = chooseFormat(texture.format());
        var decoded = texture.convert(format, true);
        var data = decoded.surfaces().getFirst().data();

        // Some games like to have textures for everything, even a single color...
        if (format != null) {
            var single = checkAllPixelsEqual(data, format);
            if (single != null) {
                var biasVector = new Vector4(Vector3.splat(bias), 0.0f);
                return new TextureAndFactor(null, single.multiply(scale).add(biasVector));
            }
        }

        if (bias == 0) {
            if (scale == 1) {
                // We just didn't touch it this time
                return new TextureAndFactor(decoded, Vector4.One);
            } else {
                // We can just scale the result
                var factor = new Vector4(Vector3.splat(scale), 1.0f);
                return new TextureAndFactor(decoded, factor);
            }
        }
        if (scale == 0) {
            // All pixels get eaten, so only a bias
            var factor = new Vector4(Vector3.splat(bias), 1.0f);
            return new TextureAndFactor(null, factor);
        }

        if (format != null) {
            // Last resort: Now we have to actually scale and shit
            scaleAndBiasArray(data, scale, bias, format);
            return new TextureAndFactor(decoded, Vector4.One);
        }

        // Can't touch it for various reasons
        return new TextureAndFactor(decoded, Vector4.One);
    }

    public TextureFormat chooseFormat(TextureFormat format) {
        return switch (format) {
            case R8_UNORM,
                 BC4_UNORM,
                 BC4_SNORM -> TextureFormat.R8_UNORM;
            case R8G8_UNORM,
                 R8G8B8_UNORM,
                 B8G8R8_UNORM,
                 BC5_UNORM,
                 BC5_SNORM -> TextureFormat.R8G8B8_UNORM;
            case R8G8B8A8_UNORM, B8G8R8A8_UNORM,
                 BC1_UNORM,
                 BC1_SRGB,
                 BC2_UNORM,
                 BC2_SRGB,
                 BC3_UNORM,
                 BC3_SRGB,
                 BC7_UNORM,
                 BC7_SRGB -> TextureFormat.R8G8B8A8_UNORM;
            default -> null;
        };
    }

    private void scaleAndBiasArray(byte[] data, float scale, float bias, TextureFormat format) {
        switch (format) {
            case R8_UNORM -> scaleAndBias1(data, scale, bias);
            case R8G8B8_UNORM -> scaleAndBias34(data, 3, scale, bias);
            case R8G8B8A8_UNORM -> scaleAndBias34(data, 4, scale, bias);
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        }
    }

    private void scaleAndBias1(byte[] data, float scale, float bias) {
        for (int i = 0; i < data.length; i++) {
            data[i] = scaleAndBias(data[i], scale, bias);
        }
    }

    private void scaleAndBias34(byte[] data, int stride, float scale, float bias) {
        for (int i = 0; i < data.length; i += stride) {
            data[i/**/] = scaleAndBias(data[i/**/], scale, bias);
            data[i + 1] = scaleAndBias(data[i + 1], scale, bias);
            data[i + 2] = scaleAndBias(data[i + 2], scale, bias);
        }
    }

    private Vector4 checkAllPixelsEqual(byte[] data, TextureFormat format) {
        return switch (format) {
            case R8_UNORM -> checkAllPixelsEqual1(data);
            case R8G8B8_UNORM -> checkAllPixelsEqual3(data);
            case R8G8B8A8_UNORM -> checkAllPixelsEqual4(data);
            default -> throw new UnsupportedOperationException();
        };
    }

    private Vector4 checkAllPixelsEqual1(byte[] data) {
        byte g = data[0];
        for (byte b : data) {
            if (b != g) {
                return null;
            }
        }

        return new Vector4(unpackSrgbToLinear(g), 0.0f, 0.0f, 1.0f);
    }

    private Vector4 checkAllPixelsEqual3(byte[] data) {
        byte r = data[0], g = data[1], b = data[2];
        for (int i = 0; i < data.length; i += 3) {
            if (data[i] != r || data[i + 1] != g || data[i + 2] != b) {
                return null;
            }
        }

        return new Vector4(unpackSrgbToLinear(r), unpackSrgbToLinear(g), unpackSrgbToLinear(b), 1.0f);
    }

    private Vector4 checkAllPixelsEqual4(byte[] data) {
        int pixel = ByteArrays.getInt(data, 0);
        for (int i = 0; i < data.length; i += 4) {
            if (ByteArrays.getInt(data, i) != pixel) {
                return null;
            }
        }

        // The factors are in linear space, not srgb
        return new Vector4(
            unpackSrgbToLinear((byte) (pixel)),
            unpackSrgbToLinear((byte) (pixel >> 8)),
            unpackSrgbToLinear((byte) (pixel >> 16)),
            unpackSrgbToLinear((byte) (pixel >> 24))
        );
    }

    private byte scaleAndBias(byte b, float scale, float bias) {
        return packLinearToSrgb(Math.fma(unpackSrgbToLinear(b), scale, bias));
    }

    private byte packLinearToSrgb(float value) {
        return MathF.packUNorm8(MathF.linearToSrgb(value));
    }

    private float unpackSrgbToLinear(byte b) {
        return MathF.srgbToLinear(MathF.unpackUNorm8(b));
    }

    private record TextureAndFactor(
        Texture texture,
        Vector4 factor
    ) {
    }

    // endregion

}
