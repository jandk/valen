package be.twofold.valen.game.dyinglight.reader.texture;

import be.twofold.valen.core.texture.*;

public enum PixelFormat {
    R8_SNORM(0, TextureFormat.R8_SNORM),
    R8_UNORM(5, TextureFormat.R8_UNORM),
    R16_SNORM(8, TextureFormat.R16_SNORM),
    R16_UNORM(15, TextureFormat.R16_UNORM),
    R8G8_SNORM(16, TextureFormat.R8G8_SNORM),
    R8G8B8A8_UNORM_SRGB(32, TextureFormat.R8G8B8A8_UNORM),
    R8G8B8A8_UNORM(38, TextureFormat.R8G8B8A8_UNORM),
    R8G8B8A8_SNORM(39, TextureFormat.R8G8B8A8_SNORM),
    R8G8B8A8_UNORM2(40, TextureFormat.R8G8B8A8_UNORM),
    R16G16B16A16_FLOAT(46, TextureFormat.R16G16B16A16_SFLOAT),
    R16G16B16A16_UNORM(47, TextureFormat.R16G16B16A16_UNORM),
    R16G16B16A16_UINT(48, TextureFormat.R16G16B16A16_UNORM),
    BC1_UNORM(59, TextureFormat.BC1_UNORM),
    BC4_SNORM(62, TextureFormat.BC4_SNORM),
    BC4_UNORM(63, TextureFormat.BC4_UNORM),
    BC5_SNORM(64, TextureFormat.BC5_SNORM),
    BC5_UNORM(65, TextureFormat.BC5_UNORM),
    BC6H_UF16(66, TextureFormat.BC6H_UFLOAT),
    BC7_UNORM(68, TextureFormat.BC7_UNORM);


    private final int internalFormat;
    private final TextureFormat textureFormat;

    PixelFormat(int internalFormat, TextureFormat textureFormat) {
        this.internalFormat = internalFormat;
        this.textureFormat = textureFormat;
    }

    public static PixelFormat fromInternalFormat(int internalFormat) {
        for (PixelFormat format : values()) {
            if (format.internalFormat == internalFormat) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown internal format: " + internalFormat);
    }

    public boolean isSrgb() {
        return switch (this) {
            case R8G8B8A8_UNORM_SRGB -> true;
            default -> false;
        };
    }

    public int internalFormat() {
        return internalFormat;
    }

    public TextureFormat textureFormat() {
        return textureFormat;
    }
}
