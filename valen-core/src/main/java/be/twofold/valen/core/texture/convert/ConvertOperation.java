package be.twofold.valen.core.texture.convert;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

public final class ConvertOperation implements Operation {
    @Override
    public Surface apply(Surface surface) {
        return switch (surface.format()) {
            case R16_UNORM -> convertU16(surface, TextureFormat.R8_UNORM);
            case R16G16B16A16_UNORM -> convertU16(surface, TextureFormat.R8G8B8A8_UNORM);
            case R16_SFLOAT -> convertF16(surface, TextureFormat.R8_UNORM);
            case R16G16_SFLOAT -> convertF16(surface, TextureFormat.R8G8_UNORM);
            case R16G16B16A16_SFLOAT -> convertF16(surface, TextureFormat.R8G8B8A8_UNORM);
            default -> surface;
        };
    }

    @Override
    public Texture map(Texture source, TextureFormat ignored) {
        var surfaces = source.surfaces().stream().map(this).toList();
        return new Texture(source.width(), source.height(), surfaces.getFirst().format(), surfaces, source.isCubeMap());
    }

    private Surface convertU16(Surface surface, TextureFormat format) {
        Surface target = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = target.data();
        for (int i = 1, o = 0; i < src.length; i += 2, o++) {
            dst[o] = src[i];
        }
        return target;
    }

    private Surface convertF16(Surface surface, TextureFormat format) {
        Surface target = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = target.data();
        for (int i = 0, o = 0; i < src.length; i += 2, o++) {
            dst[o] = MathF.packUNorm8(Float.float16ToFloat(ByteArrays.getShort(src, i)));
        }
        return target;
    }
}
