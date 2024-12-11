package be.twofold.valen.core.texture.convert;

import be.twofold.valen.core.texture.*;

public final class SwizzleOperation implements Operation {
    private final TextureFormat format;

    public SwizzleOperation(TextureFormat format) {
        this.format = format;
    }

    @Override
    public Surface apply(Surface surface) {
        if (surface.format() == format) {
            return surface;
        }

        if (surface.format() == TextureFormat.R8G8B8_UNORM) {
            if (format == TextureFormat.B8G8R8_UNORM) {
                return rgba_bgra(surface, 3);
            }
        } else if (surface.format() == TextureFormat.R8G8B8A8_UNORM) {
            if (format == TextureFormat.B8G8R8A8_UNORM) {
                return rgba_bgra(surface, 4);
            }
        } else if (surface.format() == TextureFormat.B8G8R8_UNORM) {
            if (format == TextureFormat.R8G8B8_UNORM) {
                return rgba_bgra(surface, 3);
            }
        } else if (surface.format() == TextureFormat.B8G8R8A8_UNORM) {
            if (format == TextureFormat.R8G8B8A8_UNORM) {
                return rgba_bgra(surface, 4);
            }
        }
        return null;
    }

    private static Surface rgba_bgra(Surface surface, int stride) {
        var data = surface.data();
        for (int i = 0; i < data.length; i += stride) {
            swap(data, i, i + 2);
        }
        return surface;
    }

    private static void swap(byte[] array, int i, int j) {
        byte tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}
