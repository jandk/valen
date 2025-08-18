package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;

import java.util.function.*;

final class Unpack extends Conversion {

    @Override
    Surface apply(Surface surface, TextureFormat dstFormat) {
        var srcFormat = surface.format();
        if (srcFormat == dstFormat) {
            return surface;
        }

        var unpacker = unpacker(srcFormat, dstFormat);
        if (unpacker == null) {
            return surface;
        }

        return unpack(surface, dstFormat, unpacker);
    }

    private static Surface unpack(Surface source, TextureFormat dstFormat, BiConsumer<byte[], byte[]> operation) {
        var target = Surface.create(source.width(), source.height(), dstFormat);

        var src = source.data();
        var dst = target.data();
        operation.accept(src, dst);

        return target;
    }

    // region Generated Code

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static BiConsumer<byte[], byte[]> unpacker(TextureFormat srcFormat, TextureFormat dstFormat) {
        return switch (srcFormat) {
            case R8_UNORM -> switch (dstFormat) {
                case R8G8_UNORM -> Unpack::unpackR8UnormToR8G8Unorm;
                case R8G8B8_UNORM -> Unpack::unpackR8UnormToR8G8B8Unorm;
                case R8G8B8A8_UNORM -> Unpack::unpackR8UnormToR8G8B8A8Unorm;
                case B8G8R8_UNORM -> Unpack::unpackR8UnormToB8G8R8Unorm;
                case B8G8R8A8_UNORM -> Unpack::unpackR8UnormToB8G8R8A8Unorm;
                default -> null;
            };
            case R8G8_UNORM -> switch (dstFormat) {
                case R8G8B8_UNORM -> Unpack::unpackR8G8UnormToR8G8B8Unorm;
                case R8G8B8A8_UNORM -> Unpack::unpackR8G8UnormToR8G8B8A8Unorm;
                case B8G8R8_UNORM -> Unpack::unpackR8G8UnormToB8G8R8Unorm;
                case B8G8R8A8_UNORM -> Unpack::unpackR8G8UnormToB8G8R8A8Unorm;
                default -> null;
            };
            case R8G8B8_UNORM -> switch (dstFormat) {
                case R8G8B8A8_UNORM -> Unpack::unpackR8G8B8UnormToR8G8B8A8Unorm;
                case B8G8R8A8_UNORM -> Unpack::unpackR8G8B8UnormToB8G8R8A8Unorm;
                default -> null;
            };
            case B8G8R8_UNORM -> switch (dstFormat) {
                case R8G8B8A8_UNORM -> Unpack::unpackB8G8R8UnormToR8G8B8A8Unorm;
                case B8G8R8A8_UNORM -> Unpack::unpackB8G8R8UnormToB8G8R8A8Unorm;
                default -> null;
            };
            case R16_UNORM -> switch (dstFormat) {
                case R16G16B16A16_UNORM -> Unpack::unpackR16UnormToR16G16B16A16Unorm;
                default -> null;
            };
            case R16_SFLOAT -> switch (dstFormat) {
                case R16G16_SFLOAT -> Unpack::unpackR16SfloatToR16G16Sfloat;
                case R16G16B16_SFLOAT -> Unpack::unpackR16SfloatToR16G16B16Sfloat;
                case R16G16B16A16_SFLOAT -> Unpack::unpackR16SfloatToR16G16B16A16Sfloat;
                default -> null;
            };
            case R16G16_SFLOAT -> switch (dstFormat) {
                case R16G16B16_SFLOAT -> Unpack::unpackR16G16SfloatToR16G16B16Sfloat;
                case R16G16B16A16_SFLOAT -> Unpack::unpackR16G16SfloatToR16G16B16A16Sfloat;
                default -> null;
            };
            case R16G16B16_SFLOAT -> switch (dstFormat) {
                case R16G16B16A16_SFLOAT -> Unpack::unpackR16G16B16SfloatToR16G16B16A16Sfloat;
                default -> null;
            };
            default -> null;
        };
    }

    private static void unpackR8UnormToR8G8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i++, o += 2) {
            dst[o/**/] = src[i/**/];
        }
    }

    private static void unpackR8UnormToR8G8B8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i++, o += 3) {
            dst[o/**/] = src[i/**/];
        }
    }

    private static void unpackR8UnormToR8G8B8A8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i++, o += 4) {
            dst[o/**/] = src[i/**/];
            dst[o + 3] = (byte) 0xFF;
        }
    }

    private static void unpackR8UnormToB8G8R8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i++, o += 3) {
            dst[o + 2] = src[i/**/];
        }
    }

    private static void unpackR8UnormToB8G8R8A8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i++, o += 4) {
            dst[o + 2] = src[i/**/];
            dst[o + 3] = (byte) 0xFF;
        }
    }

    private static void unpackR8G8UnormToR8G8B8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 2, o += 3) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
        }
    }

    private static void unpackR8G8UnormToR8G8B8A8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 2, o += 4) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
            dst[o + 3] = (byte) 0xFF;
        }
    }

    private static void unpackR8G8UnormToB8G8R8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 2, o += 3) {
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i/**/];
        }
    }

    private static void unpackR8G8UnormToB8G8R8A8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 2, o += 4) {
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i/**/];
            dst[o + 3] = (byte) 0xFF;
        }
    }

    private static void unpackR8G8B8UnormToR8G8B8A8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 3, o += 4) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i + 2];
            dst[o + 3] = (byte) 0xFF;
        }
    }

    private static void unpackR8G8B8UnormToB8G8R8A8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 3, o += 4) {
            dst[o/**/] = src[i + 2];
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i/**/];
            dst[o + 3] = (byte) 0xFF;
        }
    }

    private static void unpackB8G8R8UnormToR8G8B8A8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 3, o += 4) {
            dst[o/**/] = src[i + 2];
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i/**/];
            dst[o + 3] = (byte) 0xFF;
        }
    }

    private static void unpackB8G8R8UnormToB8G8R8A8Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 3, o += 4) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i + 2];
            dst[o + 3] = (byte) 0xFF;
        }
    }

    private static void unpackR16UnormToR16G16B16A16Unorm(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 2, o += 8) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
            dst[o + 6] = (byte) 0xFF;
            dst[o + 7] = (byte) 0xFF;
        }
    }

    private static void unpackR16SfloatToR16G16Sfloat(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 2, o += 4) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
        }
    }

    private static void unpackR16SfloatToR16G16B16Sfloat(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 2, o += 6) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
        }
    }

    private static void unpackR16SfloatToR16G16B16A16Sfloat(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 2, o += 8) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
            dst[o + 7] = (byte) 0x3C;
        }
    }

    private static void unpackR16G16SfloatToR16G16B16Sfloat(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 4, o += 6) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i + 2];
            dst[o + 3] = src[i + 3];
        }
    }

    private static void unpackR16G16SfloatToR16G16B16A16Sfloat(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 4, o += 8) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i + 2];
            dst[o + 3] = src[i + 3];
            dst[o + 7] = (byte) 0x3C;
        }
    }

    private static void unpackR16G16B16SfloatToR16G16B16A16Sfloat(byte[] src, byte[] dst) {
        for (int i = 0, o = 0; i < src.length; i += 6, o += 8) {
            dst[o/**/] = src[i/**/];
            dst[o + 1] = src[i + 1];
            dst[o + 2] = src[i + 2];
            dst[o + 3] = src[i + 3];
            dst[o + 4] = src[i + 4];
            dst[o + 5] = src[i + 5];
            dst[o + 7] = (byte) 0x3C;
        }
    }

    // endregion

}
