package be.twofold.valen.core.texture;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

public record Surface(
    int width,
    int height,
    int depth,
    TextureFormat format,
    Bytes data
) {
    public Surface {
        Check.positive(width, "width");
        Check.positive(height, "height");
        Check.positive(depth, "depth");
        Check.nonNull(format, "format");
        Check.nonNull(data, "data");
    }

    public static Surface create(int width, int height, TextureFormat format) {
        return create(width, height, 1, format);
    }

    public static Surface create(int width, int height, int depth, TextureFormat format) {
        Check.positive(width, "width");
        Check.positive(height, "height");
        Check.positive(depth, "depth");
        return new Surface(width, height, depth, format, Bytes.Mutable.allocate(format.surfaceSize(width, height) * depth));
    }

    public Surface withFormat(TextureFormat format) {
        return new Surface(width, height, depth, format, data);
    }

    public Bytes.Mutable mutableData() {
        if (!(data instanceof Bytes.Mutable mutable)) {
            throw new UnsupportedOperationException("Surface data is read-only");
        }
        return mutable;
    }

    public Surface withData(Bytes data) {
        return new Surface(width, height, depth, format, data);
    }

    public Surface withData(byte[] data) {
        return withData(Bytes.Mutable.wrap(data));
    }

    public static void copy(
        Surface src, int srcX, int srcY,
        Surface dst, int dstX, int dstY,
        int width, int height
    ) {
        Check.fromIndexSize(srcX, width, src.width);
        Check.fromIndexSize(srcY, height, src.height);
        Check.fromIndexSize(dstX, width, dst.width);
        Check.fromIndexSize(dstY, height, dst.height);
        Check.argument(src.format == dst.format, "format mismatch");

        var format = src.format;
        int blockWidth = format.blockWidth();
        int blockHeight = format.blockHeight();
        Check.argument(srcX % blockWidth == 0, "srcX must be a multiple of " + blockWidth);
        Check.argument(srcY % blockHeight == 0, "srcY must be a multiple of " + blockHeight);
        Check.argument(dstX % blockWidth == 0, "dstX must be a multiple of " + blockWidth);
        Check.argument(dstY % blockHeight == 0, "dstY must be a multiple of " + blockHeight);

        var srcTileX = srcX / blockWidth;
        var srcTileY = srcY / blockHeight;
        var srcTileWidth = src.width / blockWidth;

        var dstTileX = dstX / blockWidth;
        var dstTileY = dstY / blockHeight;
        var dstTileWidth = dst.width / blockWidth;

        var tileHeight = height / blockHeight;
        var tileStride = width / blockWidth * format.blockSize();

        for (var ty = 0; ty < tileHeight; ty++) {
            var srcIndex = ((srcTileY + ty) * srcTileWidth + srcTileX) * format.blockSize();
            var dstIndex = ((dstTileY + ty) * dstTileWidth + dstTileX) * format.blockSize();
            src.data.slice(srcIndex, tileStride).copyTo(dst.mutableData(), dstIndex);
        }
    }

    @Override
    public String toString() {
        return "MipLevel(" +
            "width=" + width + ", " +
            "height=" + height + ", " +
            "depth=" + depth + ", " +
            "format=" + format + ", " +
            "data=[" + data.length() + " bytes]" +
            ")";
    }
}
