package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;

final class Unpack extends Conversion {
    @Override
    Texture apply(Texture texture, TextureFormat dstFormat) {
        var srcFormat = texture.format();
        if (srcFormat == dstFormat) {
            return texture;
        }

        var operation = operation(srcFormat, dstFormat);
        if (operation == null) {
            return texture;
        }

        return map(texture, dstFormat, surface -> unpack(surface, srcFormat, dstFormat, operation));
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private Operation operation(TextureFormat srcFormat, TextureFormat dstFormat) {
        return switch (srcFormat) {
            case R8_UNORM -> switch (dstFormat) {
                case R8G8_UNORM,
                     R8G8B8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos] = src[srcPos];
                };
                case B8G8R8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos + 2] = src[srcPos];
                };
                case R8G8B8A8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos];
                    dst[dstPos + 3] = (byte) 0xFF;
                };
                case B8G8R8A8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos + 2] = src[srcPos];
                    dst[dstPos + 3] = (byte) 0xFF;
                };
                default -> null;
            };
            case R8G8_UNORM -> switch (dstFormat) {
                case R8G8B8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                };
                case B8G8R8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos/**/];
                };
                case R8G8B8A8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 3] = (byte) 0xFF;
                };
                case B8G8R8A8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos/**/];
                    dst[dstPos + 3] = (byte) 0xFF;
                };
                default -> null;
            };
            case R8G8B8_UNORM -> switch (dstFormat) {
                case R8G8B8A8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos + 2];
                    dst[dstPos + 3] = (byte) 0xFF;
                };
                case B8G8R8A8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos + 2];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos/**/];
                    dst[dstPos + 3] = (byte) 0xFF;
                };
                default -> null;
            };
            case B8G8R8_UNORM -> switch (dstFormat) {
                case R8G8B8A8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos + 2];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos/**/];
                    dst[dstPos + 3] = (byte) 0xFF;
                };
                case B8G8R8A8_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos + 2];
                    dst[dstPos + 3] = (byte) 0xFF;
                };
                default -> null;
            };
            case R16_UNORM -> switch (dstFormat) {
                case R16G16B16A16_UNORM -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 6] = (byte) 0xFF;
                    dst[dstPos + 7] = (byte) 0xFF;
                };
                default -> null;
            };
            case R16_SFLOAT -> switch (dstFormat) {
                case R16G16_SFLOAT -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                };
                case R16G16B16_SFLOAT -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos + 2];
                    dst[dstPos + 3] = src[srcPos + 3];
                };
                case R16G16B16A16_SFLOAT -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 7] = 0x3C;
                };
                default -> null;
            };
            case R16G16_SFLOAT -> switch (dstFormat) {
                case R16G16B16_SFLOAT -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos + 2];
                    dst[dstPos + 3] = src[srcPos + 3];
                };
                case R16G16B16A16_SFLOAT -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos + 2];
                    dst[dstPos + 3] = src[srcPos + 3];
                    dst[dstPos + 7] = 0x3C;
                };
                default -> null;
            };
            case R16G16B16_SFLOAT -> switch (dstFormat) {
                case R16G16B16A16_SFLOAT -> (src, srcPos, dst, dstPos) -> {
                    dst[dstPos/**/] = src[srcPos/**/];
                    dst[dstPos + 1] = src[srcPos + 1];
                    dst[dstPos + 2] = src[srcPos + 2];
                    dst[dstPos + 3] = src[srcPos + 3];
                    dst[dstPos + 4] = src[srcPos + 4];
                    dst[dstPos + 5] = src[srcPos + 5];
                    dst[dstPos + 7] = 0x3C;
                };
                default -> null;
            };
            default -> null;
        };
    }

    private static Surface unpack(Surface source, TextureFormat srcFormat, TextureFormat dstFormat, Operation operation) {
        var target = Surface.create(source.width(), source.height(), dstFormat);

        var src = source.data();
        var dst = target.data();
        var srcStride = srcFormat.block().size();
        var dstStride = dstFormat.block().size();
        for (int i = 0, o = 0; i < src.length; i += srcStride, o += dstStride) {
            operation.apply(src, i, dst, o);
        }

        return target;
    }

    @FunctionalInterface
    private interface Operation {
        void apply(byte[] src, int srcPos, byte[] dst, int dstPos);
    }
}
