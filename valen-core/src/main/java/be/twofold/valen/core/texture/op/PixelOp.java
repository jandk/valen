package be.twofold.valen.core.texture.op;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.texture.*;

public interface PixelOp {
    static PixelOp source(Surface input) {
        if (input.format().isCompressed()) {
            var blockFormat = switch (input.format()) {
                case BC1_SRGB, BC1_UNORM -> BlockFormat.BC1;
                case BC2_SRGB, BC2_UNORM -> BlockFormat.BC2;
                case BC3_SRGB, BC3_UNORM -> BlockFormat.BC3;
                case BC4_SNORM -> BlockFormat.BC4Signed;
                case BC4_UNORM -> BlockFormat.BC4Unsigned;
                case BC5_SNORM -> BlockFormat.BC5Signed;
                case BC5_UNORM -> BlockFormat.BC5Unsigned;
                case BC6H_SFLOAT -> BlockFormat.BC6Signed;
                case BC6H_UFLOAT -> BlockFormat.BC6Unsigned;
                case BC7_SRGB, BC7_UNORM -> BlockFormat.BC7;
                default -> throw new UnsupportedOperationException("Unsupported format: " + input.format());
            };

            var format = input.format().block() == TextureFormat.Block.BC6H
                ? TextureFormat.R16G16B16A16_SFLOAT
                : TextureFormat.R8G8B8A8_UNORM;

            var decoded = Surface.create(input.width(), input.height(), format);
            BlockDecoder
                .create(blockFormat, PixelOrder.RGBA)
                .decode(input.width(), input.height(), input.data(), 0, decoded.data(), 0);

            if (input.format().block() == TextureFormat.Block.BC6H) {
                return F16PixelOp.source(decoded);
            }
            return U8PixelOp.source(decoded);
        }

        return switch (input.format()) {
            case R8_UNORM, R8G8_UNORM, R8G8B8_UNORM, R8G8B8A8_UNORM, B8G8R8_UNORM, B8G8R8A8_UNORM ->
                U8PixelOp.source(input);
            case R16_UNORM -> U16PixelOp.source(input);
            case R16_SFLOAT, R16G16_SFLOAT, R16G16B16A16_SFLOAT -> F16PixelOp.source(input);
            default -> throw new UnsupportedOperationException("Unsupported format: " + input.format());
        };
    }

    U8PixelOp asU8();

    Surface toSurface(int width, int height, TextureFormat format);
}
