package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;

import java.util.function.*;

final class Swizzle extends Conversion {
    @Override
    Texture apply(Texture texture, TextureFormat dstFormat) {
        if (texture.format() == dstFormat) {
            return texture;
        }

        var operator = operator(texture.format(), dstFormat);
        if (operator == null) {
            return texture;
        }

        return map(texture, dstFormat, operator);
    }

    private UnaryOperator<Surface> operator(TextureFormat srcFormat, TextureFormat dstFormat) {
        if (srcFormat == TextureFormat.R8G8B8_UNORM && dstFormat == TextureFormat.B8G8R8_UNORM ||
            srcFormat == TextureFormat.B8G8R8_UNORM && dstFormat == TextureFormat.R8G8B8_UNORM) {
            return surface -> rgba_bgra(surface, 3);
        }
        if (srcFormat == TextureFormat.R8G8B8A8_UNORM && dstFormat == TextureFormat.B8G8R8A8_UNORM ||
            srcFormat == TextureFormat.B8G8R8A8_UNORM && dstFormat == TextureFormat.R8G8B8A8_UNORM) {
            return surface -> rgba_bgra(surface, 4);
        }
        return null;
    }

    private Surface rgba_bgra(Surface surface, int stride) {
        var data = surface.data();
        for (int i = 0; i < data.length; i += stride) {
            swap(data, i, i + 2);
        }
        return surface;
    }

    private void swap(byte[] array, int i, int j) {
        byte tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}
