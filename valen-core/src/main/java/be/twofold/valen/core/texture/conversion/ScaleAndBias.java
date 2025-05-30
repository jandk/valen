package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import org.slf4j.*;

final class ScaleAndBias extends Conversion {
    private static final Logger log = LoggerFactory.getLogger(ScaleAndBias.class);

    @Override
    Texture apply(Texture texture, TextureFormat dstFormat) {
        if (texture.format() != dstFormat) {
            throw new IllegalArgumentException("source format does not match target format");
        }

        var scale = texture.scale();
        var bias = texture.bias();
        if (bias == 0 && scale == 1) {
            return texture;
        }

        log.info("Applying scale {} and bias {}", scale, bias);
        return map(texture, dstFormat, surface -> {
            scaleAndBiasArray(surface.data(), scale, bias, dstFormat);
            return surface;
        }).withScaleAndBias(1.0f, 0.0f);
    }

    private void scaleAndBiasArray(byte[] data, float scale, float bias, TextureFormat format) {
        var table = generateTable(scale, bias);
        switch (format) {
            case R8_UNORM -> scaleAndBiasR(data, table);
            case R8G8_UNORM -> scaleAndBiasRG(data, 2, table);
            case R8G8B8_UNORM, B8G8R8_UNORM -> scaleAndBiasRGB(data, 3, table);
            case R8G8B8A8_UNORM, B8G8R8A8_UNORM -> scaleAndBiasRGB(data, 4, table);
            default -> throw uoe(format);
        }
    }

    private void scaleAndBiasR(byte[] data, byte[] table) {
        for (int i = 0; i < data.length; i++) {
            data[i] = table[Byte.toUnsignedInt(data[i])];
        }
    }

    private void scaleAndBiasRG(byte[] data, int stride, byte[] table) {
        for (int i = 0; i < data.length; i += stride) {
            data[i/**/] = table[Byte.toUnsignedInt(data[i/**/])];
            data[i + 1] = table[Byte.toUnsignedInt(data[i + 1])];
        }
    }

    private void scaleAndBiasRGB(byte[] data, int stride, byte[] table) {
        for (int i = 0; i < data.length; i += stride) {
            data[i/**/] = table[Byte.toUnsignedInt(data[i/**/])];
            data[i + 1] = table[Byte.toUnsignedInt(data[i + 1])];
            data[i + 2] = table[Byte.toUnsignedInt(data[i + 2])];
        }
    }

    private static byte[] generateTable(float scale, float bias) {
        byte[] table = new byte[256];
        for (int i = 0; i < 256; i++) {
            table[i] = scaleAndBias((byte) i, scale, bias);
        }
        return table;
    }

    private static byte scaleAndBias(byte b, float scale, float bias) {
        float f = srgbToLinear(MathF.unpackUNorm8(b));
        f = Math.fma(f, scale, bias);
        return MathF.packUNorm8(linearToSrgb(f));
    }

    private static float linearToSrgb(float f) {
        if (f <= (0.04045f / 12.92f)) {
            return f * 12.92f;
        }
        return Math.fma(MathF.pow(f, 1.0f / 2.4f), 1.055f, -0.055f);
    }

    private static float srgbToLinear(float f) {
        if (f <= 0.04045f) {
            return f * (1.0f / 12.92f);
        }
        return MathF.pow(Math.fma(f, 1.0f / 1.055f, 0.055f / 1.055f), 2.4f);
    }
}
