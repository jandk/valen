package be.twofold.valen.core.texture;

import be.twofold.valen.core.util.*;

public record Surface(
    int width,
    int height,
    TextureFormat format,
    byte[] data
) {
    public Surface {
        Check.positive(width, "width");
        Check.positive(height, "height");
        Check.nonNull(format, "format");
        Check.nonNull(data, "data");
    }

    public static Surface create(int width, int height, TextureFormat format) {
        Check.positive(width, "width");
        Check.positive(height, "height");

        var data = new byte[format.surfaceSize(width, height)];
        return new Surface(width, height, format, data);
    }

    public Surface withFormat(TextureFormat format) {
        return new Surface(width, height, format, data);
    }

    public Surface withData(byte[] data) {
        return new Surface(width, height, format, data);
    }


    //    public void copyFrom(Surface src, int x, int y) {
//        Check.fromIndexSize(x, src.width, width);
//        Check.fromIndexSize(y, src.height, height);
//        Check.argument(x % format.block().width() == 0, "x must be a multiple of " + format.block().width());
//        Check.argument(y % format.block().height() == 0, "y must be a multiple of " + format.block().height());
//        Check.argument(format == src.format, "format mismatch");
//
//        var dstOffsetX = x / format.block().width();
//        var dstOffsetY = y / format.block().height();
//        var srcCountBytes = src.tileCountX() * format.block().size();
//
//        for (var tileY = 0; tileY < src.tileCountY(); tileY++) {
//            var srcIndex = tileY * srcCountBytes;
//            var dstIndex = ((dstOffsetY + tileY) * tileCountX() + dstOffsetX) * format.block().size();
//            System.arraycopy(src.data, srcIndex, data, dstIndex, srcCountBytes);
//        }
//    }
//
//    public int tileCountX() {
//        return width / format.block().width();
//    }
//
//    public int tileCountY() {
//        return height / format.block().height();
//    }
//
    @Override
    public String toString() {
        return "Surface(" +
            "width=" + width + ", " +
            "height=" + height + ", " +
            "format=" + format + ", " +
            "data=[" + data.length + " bytes]" +
            ")";
    }
}
