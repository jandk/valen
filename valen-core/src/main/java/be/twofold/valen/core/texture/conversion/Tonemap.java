package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

final class Tonemap extends Conversion {
    @Override
    Texture apply(Texture texture, TextureFormat dstFormat) {
        var operatorFormat = operator(texture.format());
        if (operatorFormat == null) {
            return texture;
        }

        return map(texture, operatorFormat.format(), operatorFormat.operator());
    }

    private OperatorFormat operator(TextureFormat format) {
        return switch (format) {
            case R16_UNORM ->
                new OperatorFormat(surface -> tonemapU16(surface, TextureFormat.R8_UNORM), TextureFormat.R8_UNORM);
            case R16G16B16A16_UNORM ->
                new OperatorFormat(surface -> tonemapU16(surface, TextureFormat.R8G8B8A8_UNORM), TextureFormat.R8G8B8A8_UNORM);
            case R16_SFLOAT ->
                new OperatorFormat(surface -> tonemapF16(surface, TextureFormat.R8_UNORM), TextureFormat.R8_UNORM);
            case R16G16_SFLOAT ->
                new OperatorFormat(surface -> tonemapF16(surface, TextureFormat.R8G8_UNORM), TextureFormat.R8G8_UNORM);
            case R16G16B16_SFLOAT ->
                new OperatorFormat(surface -> tonemapF16(surface, TextureFormat.R8G8B8_UNORM), TextureFormat.R8G8B8_UNORM);
            case R16G16B16A16_SFLOAT ->
                new OperatorFormat(surface -> tonemapF16(surface, TextureFormat.R8G8B8A8_UNORM), TextureFormat.R8G8B8A8_UNORM);
            default -> null;
        };
    }

    private Surface tonemapU16(Surface surface, TextureFormat format) {
        Surface target = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = target.data();
        for (int i = 1, o = 0; i < src.length; i += 2, o++) {
            dst[o] = src[i];
        }
        return target;
    }

    private Surface tonemapF16(Surface surface, TextureFormat format) {
        Surface target = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = target.data();
        for (int i = 0, o = 0; i < src.length; i += 2, o++) {
            dst[o] = MathF.packUNorm8(Float.float16ToFloat(ByteArrays.getShort(src, i)));
        }
        return target;
    }
}
