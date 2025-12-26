package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.util.*;

final class ScaleAndBias extends Conversion {
    private final float scale;
    private final float bias;
    private final byte[] table;

    ScaleAndBias(float scale, float bias) {
        this.scale = scale;
        this.bias = bias;
        this.table = generateTable(scale, bias);
    }

    @Override
    Surface apply(Surface surface, TextureFormat dstFormat) {
        Check.argument(surface.format() == dstFormat, "source format does not match target format");
        if (scale == 1.0f && bias == 0.0f) {
            return surface;
        }

        scaleAndBiasArray(surface.data(), dstFormat);
        return surface;
    }

    private void scaleAndBiasArray(byte[] data, TextureFormat format) {
        switch (format) {
            case R8_UNORM -> scaleAndBiasR(data);
            case R8G8_UNORM -> scaleAndBiasRG(data);
            case R8G8B8_UNORM, B8G8R8_UNORM -> scaleAndBiasRGB(data, 3);
            case R8G8B8A8_UNORM, B8G8R8A8_UNORM -> scaleAndBiasRGB(data, 4);
            default -> throw uoe(format);
        }
    }

    private void scaleAndBiasR(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = table[Byte.toUnsignedInt(data[i])];
        }
    }

    private void scaleAndBiasRG(byte[] data) {
        for (int i = 0; i < data.length; i += 2) {
            data[i/**/] = table[Byte.toUnsignedInt(data[i/**/])];
            data[i + 1] = table[Byte.toUnsignedInt(data[i + 1])];
        }
    }

    private void scaleAndBiasRGB(byte[] data, int stride) {
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
        float f = MathF.srgbToLinear(MathF.unpackUNorm8(b));
        f = Math.fma(f, scale, bias);
        return MathF.packUNorm8(MathF.linearToSrgb(f));
    }
}
