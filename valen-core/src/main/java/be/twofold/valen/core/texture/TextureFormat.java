package be.twofold.valen.core.texture;

public enum TextureFormat {
    // Uncompressed formats
    R8_UNORM,
    R8_SNORM,
    R8G8_UNORM,
    R8G8_SNORM,
    R8G8B8_UNORM,
    R8G8B8A8_UNORM,
    R8G8B8A8_SNORM,
    B8G8R8_UNORM,
    B8G8R8A8_UNORM,
    R16_UNORM,
    R16_SNORM,
    R16G16B16A16_UNORM,
    R16_SFLOAT,
    R16G16_SFLOAT,
    R16G16B16_SFLOAT,
    R16G16B16A16_SFLOAT,

    // Compressed formats
    BC1_UNORM,
    BC1_SRGB,
    BC2_UNORM,
    BC2_SRGB,
    BC3_UNORM,
    BC3_SRGB,
    BC4_UNORM,
    BC4_SNORM,
    BC5_UNORM,
    BC5_SNORM,
    BC6H_UFLOAT,
    BC6H_SFLOAT,
    BC7_UNORM,
    BC7_SRGB,
    ;

    public int blockWidth() {
        return isCompressed() ? 4 : 1;
    }

    public int blockHeight() {
        return isCompressed() ? 4 : 1;
    }

    public int blockSize() {
        return switch (this) {
            case R8_UNORM, R8_SNORM -> 1;
            case R8G8_UNORM, R8G8_SNORM,
                 R16_UNORM, R16_SNORM, R16_SFLOAT -> 2;
            case R8G8B8_UNORM, B8G8R8_UNORM -> 3;
            case R8G8B8A8_UNORM, B8G8R8A8_UNORM,
                 R8G8B8A8_SNORM,
                 R16G16_SFLOAT -> 4;
            case R16G16B16A16_UNORM, R16G16B16A16_SFLOAT,
                 BC1_UNORM, BC1_SRGB,
                 BC4_UNORM, BC4_SNORM -> 8;
            case R16G16B16_SFLOAT -> 6;
            case BC2_UNORM, BC2_SRGB,
                 BC3_UNORM, BC3_SRGB,
                 BC5_UNORM, BC5_SNORM,
                 BC6H_UFLOAT, BC6H_SFLOAT,
                 BC7_UNORM, BC7_SRGB -> 16;
        };
    }

    public boolean hasAlpha() {
        return switch (this) {
            case R8G8B8A8_UNORM, R8G8B8A8_SNORM, B8G8R8A8_UNORM,
                 R16G16B16A16_UNORM, R16G16B16A16_SFLOAT,
                 BC1_UNORM, BC1_SRGB,
                 BC2_UNORM, BC2_SRGB,
                 BC3_UNORM, BC3_SRGB,
                 BC7_UNORM, BC7_SRGB -> true;
            default -> false;
        };
    }

    public boolean isCompressed() {
        return switch (this) {
            case BC1_SRGB, BC1_UNORM,
                 BC2_SRGB, BC2_UNORM,
                 BC3_SRGB, BC3_UNORM,
                 BC4_SNORM, BC4_UNORM,
                 BC5_SNORM, BC5_UNORM,
                 BC6H_SFLOAT, BC6H_UFLOAT,
                 BC7_SRGB, BC7_UNORM -> true;
            default -> false;
        };
    }

    public int surfaceSize(int width, int height) {
        var widthInBlocks = Math.ceilDiv(width, blockWidth());
        var heightInBlocks = Math.ceilDiv(height, blockHeight());
        return widthInBlocks * heightInBlocks * blockSize();
    }
}
