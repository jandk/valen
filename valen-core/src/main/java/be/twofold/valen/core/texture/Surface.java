package be.twofold.valen.core.texture;

import be.twofold.valen.core.util.*;

public record Surface(
    int width,
    int height,
    byte[] data
) {
    public Surface {
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.notNull(data, "data is null");
    }

    public static Surface create(int width, int height, TextureFormat format) {
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");

        var data = new byte[format.block().surfaceSize(width, height)];
        return new Surface(width, height, data);
    }

    public Surface withData(byte[] data) {
        return new Surface(width, height, data);
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
            "data=[" + data.length + " bytes]" +
            ")";
    }
}
