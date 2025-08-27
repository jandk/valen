package be.twofold.valen.core.texture;

public enum TextureFormat {
    R8_UNORM(Block.Bits8, Interp.UNorm),
    R8G8_UNORM(Block.Bits16, Interp.UNorm),
    R8G8B8_UNORM(Block.Bits24, Interp.UNorm),
    R8G8B8A8_UNORM(Block.Bits32, Interp.UNorm),
    B8G8R8_UNORM(Block.Bits24, Interp.UNorm),
    B8G8R8A8_UNORM(Block.Bits32, Interp.UNorm),
    R16_UNORM(Block.Bits16, Interp.UNorm),
    R16G16B16A16_UNORM(Block.Bits64, Interp.UNorm),
    R16_SFLOAT(Block.Bits16, Interp.SFloat),
    R16G16_SFLOAT(Block.Bits32, Interp.SFloat),
    R16G16B16_SFLOAT(Block.Bits48, Interp.SFloat),
    R16G16B16A16_SFLOAT(Block.Bits64, Interp.SFloat),

    BC1_UNORM(Block.BC1, Interp.UNorm),
    BC1_SRGB(Block.BC1, Interp.SRGB),
    BC2_UNORM(Block.BC2, Interp.UNorm),
    BC2_SRGB(Block.BC2, Interp.SRGB),
    BC3_UNORM(Block.BC3, Interp.UNorm),
    BC3_SRGB(Block.BC3, Interp.SRGB),
    BC4_UNORM(Block.BC4, Interp.UNorm),
    BC4_SNORM(Block.BC4, Interp.SNorm),
    BC5_UNORM(Block.BC5, Interp.UNorm),
    BC5_SNORM(Block.BC5, Interp.SNorm),
    BC6H_UFLOAT(Block.BC6H, Interp.UFloat),
    BC6H_SFLOAT(Block.BC6H, Interp.SFloat),
    BC7_UNORM(Block.BC7, Interp.UNorm),
    BC7_SRGB(Block.BC7, Interp.SRGB);

    private final Block block;
    private final Interp interp;

    TextureFormat(Block block, Interp interp) {
        this.block = block;
        this.interp = interp;
    }

    public Block block() {
        return block;
    }

    public Interp interp() {
        return interp;
    }

    public boolean hasAlpha() {
        return switch (this) {
            case R8G8B8A8_UNORM, B8G8R8A8_UNORM,
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

    public enum Block {
        Bits8(1, 1, 1),
        Bits16(2, 1, 1),
        Bits24(3, 1, 1),
        Bits32(4, 1, 1),
        Bits48(6, 1, 1),
        Bits64(8, 1, 1),
        BC1(8, 4, 4),
        BC2(16, 4, 4),
        BC3(16, 4, 4),
        BC4(8, 4, 4),
        BC5(16, 4, 4),
        BC6H(16, 4, 4),
        BC7(16, 4, 4);

        private final int size;
        private final int width;
        private final int height;

        Block(int size, int width, int height) {
            this.size = size;
            this.width = width;
            this.height = height;
        }

        public int size() {
            return size;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public int surfaceSize(int width, int height) {
            var blockWidth = (width + this.width - 1) / this.width;
            var blockHeight = (height + this.height - 1) / this.height;
            return blockWidth * blockHeight * size;
        }
    }

    public enum Interp {
        SFloat,
        SNorm,
        SRGB,
        UFloat,
        UNorm,
    }

}
