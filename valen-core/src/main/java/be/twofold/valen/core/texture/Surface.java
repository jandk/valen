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
        Check.argument(format.blockFormat().surfaceSize(width, height) == data.length, "data length mismatch");
    }

    public static Surface create(int width, int height, TextureFormat format) {
        var blockFormat = format.blockFormat();
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.argument(width % blockFormat.width() == 0, "width must be a multiple of " + blockFormat.width());
        Check.argument(height % blockFormat.height() == 0, "height must be a multiple of " + blockFormat.height());

        var data = new byte[blockFormat.surfaceSize(width, height)];
        return new Surface(width, height, format, data);
    }

    public void copyFrom(Surface src, int x, int y) {
        var blockFormat = format.blockFormat();
        Check.fromIndexSize(x, src.width, width);
        Check.fromIndexSize(y, src.height, height);
        Check.argument(x % blockFormat.width() == 0, "x must be a multiple of " + blockFormat.width());
        Check.argument(y % blockFormat.height() == 0, "y must be a multiple of " + blockFormat.height());
        Check.argument(format == src.format, "format mismatch");

        var dstOffsetX = x / blockFormat.width();
        var dstOffsetY = y / blockFormat.height();
        var srcCountBytes = src.tileCountX() * blockFormat.size();

        for (var tileY = 0; tileY < src.tileCountY(); tileY++) {
            var srcIndex = tileY * srcCountBytes;
            var dstIndex = ((dstOffsetY + tileY) * tileCountX() + dstOffsetX) * blockFormat.size();
            System.arraycopy(src.data, srcIndex, data, dstIndex, srcCountBytes);
        }
    }

    public int tileCountX() {
        return width / format.blockFormat().width();
    }

    public int tileCountY() {
        return height / format.blockFormat().height();
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
