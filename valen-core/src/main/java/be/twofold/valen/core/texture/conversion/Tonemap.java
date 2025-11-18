package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.collect.*;

import java.util.function.*;

final class Tonemap extends Conversion {
    private static final byte[] HALF_SRGB = new byte[0x3C01];

    static {
        for (int i = 0; i <= 0x3C00; i++) {
            HALF_SRGB[i] = MathF.packUNorm8(MathF.linearToSrgb(Float.float16ToFloat((short) i)));
        }
    }

    @Override
    Surface apply(Surface surface, TextureFormat dstFormat) {
        var operatorFormat = operator(surface.format());
        if (operatorFormat == null) {
            return surface;
        }

        return operatorFormat.apply(surface);
    }

    private UnaryOperator<Surface> operator(TextureFormat format) {
        return switch (format) {
            case R16_UNORM -> surface -> toneMapU16(surface, TextureFormat.R8_UNORM);
            case R16G16B16A16_UNORM -> surface -> toneMapU16(surface, TextureFormat.R8G8B8A8_UNORM);
            case R16_SFLOAT -> surface -> toneMapF16(surface, TextureFormat.R8_UNORM);
            case R16G16_SFLOAT -> surface -> toneMapF16(surface, TextureFormat.R8G8_UNORM);
            case R16G16B16_SFLOAT -> surface -> toneMapF16(surface, TextureFormat.R8G8B8_UNORM);
            case R16G16B16A16_SFLOAT -> surface -> toneMapF16(surface, TextureFormat.R8G8B8A8_UNORM);
            default -> null;
        };
    }

    private Surface toneMapU16(Surface surface, TextureFormat format) {
        Surface target = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = target.data();
        for (int i = 1, o = 0; i < src.length; i += 2, o++) {
            dst[o] = src[i];
        }
        return target;
    }

    private Surface toneMapF16(Surface surface, TextureFormat format) {
        Surface target = Surface.create(surface.width(), surface.height(), format);

        var src = Bytes.wrap(surface.data());
        var dst = target.data();
        for (int i = 0, o = 0; i < src.size(); i += 2, o++) {
            dst[o] = halfToSrgb(src.getShort(i));
        }
        return target;
    }

    private static byte halfToSrgb(short s) {
        // If it's < 0, the float value is also < 0.0
        // If it's > 0x7C00, it's NaN
        if (s < 0 || s > 0x7C00) {
            return 0x00;
        }
        // If it's > 0x3C00, it's bigger than 1.0
        if (s > 0x3C00) {
            return (byte) 0xFF;
        }
        return HALF_SRGB[s];
    }
}
