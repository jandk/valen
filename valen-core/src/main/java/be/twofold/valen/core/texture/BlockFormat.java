package be.twofold.valen.core.texture;

import be.twofold.valen.core.util.*;

public enum BlockFormat {
    _8(1, 1, 1),
    _16(2, 1, 1),
    _32(4, 1, 1),
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

    BlockFormat(int size, int width, int height) {
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
        Check.argument(width % this.width == 0, "width must be a multiple of " + this.width);
        Check.argument(height % this.height == 0, "height must be a multiple of " + this.height);
        return (width / this.width) * (height / this.height) * this.size;
    }
}
