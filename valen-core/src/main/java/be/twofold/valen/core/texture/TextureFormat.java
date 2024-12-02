package be.twofold.valen.core.texture;

import java.util.*;

public enum TextureFormat {
    R8_UNORM(Block.Bits8, Interp.UNorm, Order.R),
    R8G8_UNORM(Block.Bits16, Interp.UNorm, Order.RG),
    R8G8B8_UNORM(Block.Bits24, Interp.UNorm, Order.RGB),
    R8G8B8A8_UNORM(Block.Bits32, Interp.UNorm, Order.RGBA),
    B8G8R8_UNORM(Block.Bits24, Interp.UNorm, Order.BGR),
    B8G8R8A8_UNORM(Block.Bits32, Interp.UNorm, Order.BGRA),
    R16_SFLOAT(Block.Bits16, Interp.SFloat, Order.R),
    R16G16B16A16_SFLOAT(Block.Bits64, Interp.SFloat, Order.RGBA),
    R16_UNORM(Block.Bits16, Interp.UNorm, Order.R),
    R16G16_SFLOAT(Block.Bits32, Interp.SFloat, Order.RG),
    R32_SFLOAT(Block.Bits32, Interp.SFloat, Order.R),
    R32G32_SFLOAT(Block.Bits64, Interp.SFloat, Order.RGBA),
    R32G32B32A32_SFLOAT(Block.Bits128, Interp.SFloat, Order.RGBA),

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
    private final Order order;

    TextureFormat(Block block, Interp interp) {
        this(block, interp, null);
    }

    TextureFormat(Block block, Interp interp, Order order) {
        this.block = block;
        this.interp = interp;
        this.order = order;
    }

    public Block block() {
        return block;
    }

    public Interp interp() {
        return interp;
    }

    public Optional<Order> order() {
        return Optional.ofNullable(order);
    }

    public boolean isCompressed() {
        return order().isEmpty();
    }

    public enum Block {
        Bits8(1, 1, 1),
        Bits16(2, 1, 1),
        Bits24(3, 1, 1),
        Bits32(4, 1, 1),
        Bits64(8, 1, 1),
        Bits128(16, 1, 1),
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

    public enum Order {
        R(1, 0, -1, -1, -1),
        RG(2, 0, 1, -1, -1),
        RGB(3, 0, 1, 2, -1),
        RGBA(4, 0, 1, 2, 3),
        BGR(3, 2, 1, 0, -1),
        BGRA(4, 2, 1, 0, 3),
        ABGR(4, 3, 2, 1, 0);

        private final int count;
        private final int r;
        private final int g;
        private final int b;
        private final int a;

        Order(int count, int r, int g, int b, int a) {
            this.count = count;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public int count() {
            return count;
        }

        public int r() {
            return r;
        }

        public int g() {
            return g;
        }

        public int b() {
            return b;
        }

        public int a() {
            return a;
        }
    }
}
