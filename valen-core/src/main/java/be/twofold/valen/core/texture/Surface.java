package be.twofold.valen.core.texture;

import be.twofold.valen.core.util.*;

public record Surface(
    int width,
    int height,
    TextureFormat format,
    byte[] data
) {
    public Surface {
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.notNull(format, "format is null");
        Check.notNull(data, "data is null");

        // TODO: check data length
        // Can't do that right now, because we might need the actual format,
        // but even then calculating the actual length is a PITA for a lot of formats
    }

    public static Surface create(int width, int height, TextureFormat format) {
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.argument(width % format.tileWidth() == 0, "width must be a multiple of " + format.tileWidth());
        Check.argument(height % format.tileHeight() == 0, "height must be a multiple of " + format.tileHeight());

        var data = new byte[format.surfaceSize(width, height)];
        return new Surface(width, height, format, data);
    }

    public void copyFrom(Surface src, int x, int y) {
        Check.fromIndexSize(x, src.width, width);
        Check.fromIndexSize(y, src.height, height);
        Check.argument(x % format.tileWidth() == 0, "x must be a multiple of " + format.tileWidth());
        Check.argument(y % format.tileHeight() == 0, "y must be a multiple of " + format.tileHeight());
        Check.argument(format == src.format, "format mismatch");

        var dstOffsetX = x / format.tileWidth();
        var dstOffsetY = y / format.tileHeight();
        var srcCountBytes = src.tileCountX() * format.tileSizeInBytes();

        for (var tileY = 0; tileY < src.tileCountY(); tileY++) {
            var srcIndex = tileY * srcCountBytes;
            var dstIndex = ((dstOffsetY + tileY) * tileCountX() + dstOffsetX) * format.tileSizeInBytes();
            System.arraycopy(src.data, srcIndex, data, dstIndex, srcCountBytes);
        }
    }

    public int tileCountX() {
        return width / format.tileWidth();
    }

    public int tileCountY() {
        return height / format.tileHeight();
    }

    @Override
    public String toString() {
        return "Surface(" +
            "width=" + width + ", " +
            "height=" + height + ", " +
            "data=[" + data.length + " bytes]" +
            ")";
    }
}
