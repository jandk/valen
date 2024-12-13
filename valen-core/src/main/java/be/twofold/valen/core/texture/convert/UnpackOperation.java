package be.twofold.valen.core.texture.convert;

import be.twofold.valen.core.texture.*;

public final class UnpackOperation implements Operation {
    private final TextureFormat format;

    public UnpackOperation(TextureFormat format) {
        this.format = format;
    }

    @Override
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public Surface apply(Surface surface) {
        if (surface.format() == format) {
            return surface;
        }
        switch (surface.format()) {
            case R8_UNORM -> {
                switch (format) {
                    case R8G8_UNORM -> {
                        return unpack(surface, TextureFormat.R8G8_UNORM, 1, new byte[]{0x00});
                    }
                    case R8G8B8_UNORM -> {
                        return unpack(surface, TextureFormat.R8G8B8_UNORM, 1, new byte[]{0x00, 0x00});
                    }
                    case R8G8B8A8_UNORM -> {
                        return unpack(surface, TextureFormat.R8G8B8A8_UNORM, 1, new byte[]{0x00, 0x00, (byte) 0xFF});
                    }
                }
            }
            case R8G8_UNORM -> {
                switch (format) {
                    case R8G8B8_UNORM -> {
                        return unpack(surface, TextureFormat.R8G8B8_UNORM, 2, new byte[]{0x00});
                    }
                    case R8G8B8A8_UNORM -> {
                        return unpack(surface, TextureFormat.R8G8B8A8_UNORM, 2, new byte[]{0x00, (byte) 0xFF});
                    }
                }
            }
            case R8G8B8_UNORM -> {
                switch (format) {
                    case R8G8B8A8_UNORM -> {
                        return unpack(surface, TextureFormat.R8G8B8A8_UNORM, 3, new byte[]{(byte) 0xFF});
                    }
                }
            }
            case B8G8R8_UNORM -> {
                switch (format) {
                    case B8G8R8A8_UNORM -> {
                        return unpack(surface, TextureFormat.B8G8R8A8_UNORM, 3, new byte[]{(byte) 0xFF});
                    }
                }
            }
            case R16_UNORM -> {
                switch (format) {
                    case R16G16B16A16_UNORM -> {
                        return unpack(surface, TextureFormat.R16G16B16A16_UNORM, 2, new byte[]{0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF});
                    }
                }
            }
            case R16_SFLOAT -> {
                switch (format) {
                    case R16G16_SFLOAT -> {
                        return unpack(surface, TextureFormat.R16G16_SFLOAT, 2, new byte[]{0x00, 0x00});
                    }
                    case R16G16B16A16_SFLOAT -> {
                        return unpack(surface, TextureFormat.R16G16B16A16_SFLOAT, 2, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x3C});
                    }
                }
            }
            case R16G16_SFLOAT -> {
                switch (format) {
                    case R16G16B16A16_SFLOAT -> {
                        return unpack(surface, TextureFormat.R16G16B16A16_SFLOAT, 4, new byte[]{0x00, 0x00, 0x00, 0x3C});
                    }
                }
            }
        }
        return surface;
    }

    private static Surface unpack(Surface surface, TextureFormat format, int stride, byte[] filler) {
        var result = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = result.data();

        for (int i = 0, o = 0; i < src.length; i += stride) {
            System.arraycopy(src, i, dst, o, stride);
            o += stride;
            System.arraycopy(filler, 0, dst, o, filler.length);
            o += filler.length;
        }

        return result;
    }
}
