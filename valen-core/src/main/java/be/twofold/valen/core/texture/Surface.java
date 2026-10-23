package be.twofold.valen.core.texture;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

public record Surface(
    TextureFormat format,
    int width,
    int height,
    int depth,
    Bytes data
) {
    public Surface {
        Check.nonNull(format, "format");
        Check.positive(width, "width");
        Check.positive(height, "height");
        Check.positive(depth, "depth");
        Check.nonNull(data, "data");
        int expectedSize = format.surfaceSize(width, height, depth);
        Check.argument(data.length() == expectedSize,
            () -> String.format("Expected %d bytes, got %d", expectedSize, data.length()));
    }

    public static Surface create(int width, int height, int depth, TextureFormat format) {
        return new Surface(format, width, height, depth, Bytes.allocate(format.surfaceSize(width, height, depth)));
    }

    public int offset(int x, int y, int z) {
        return ((z * height + y) * width + x) * format.blockSize();
    }

    public int sliceOffset(int slice) {
        Check.index(slice, depth);
        return format.surfaceSize(width, height, slice);
    }

    public static void copy(
        Surface src, int srcX, int srcY,
        Surface dst, int dstX, int dstY,
        int width, int height
    ) {
        copyCheck(src, srcX, srcY, width, height);
        copyCheck(dst, dstX, dstY, width, height);
        Check.argument(src.format == dst.format, "format mismatch");

        var format = src.format;
        int blockWidth = format.blockWidth();
        int blockHeight = format.blockHeight();
        Check.argument(srcX % blockWidth == 0, "srcX must be a multiple of " + blockWidth);
        Check.argument(srcY % blockHeight == 0, "srcY must be a multiple of " + blockHeight);
        Check.argument(dstX % blockWidth == 0, "dstX must be a multiple of " + blockWidth);
        Check.argument(dstY % blockHeight == 0, "dstY must be a multiple of " + blockHeight);

        var srcTileX = Math.ceilDiv(srcX, blockWidth);
        var srcTileY = Math.ceilDiv(srcY, blockHeight);
        var srcTileWidth = src.width / blockWidth;

        var dstTileX = Math.ceilDiv(dstX, blockWidth);
        var dstTileY = Math.ceilDiv(dstY, blockHeight);
        var dstTileWidth = dst.width / blockWidth;

        var tileStride = Math.ceilDiv(width, blockWidth) * format.blockSize();
        var tileHeight = Math.ceilDiv(height, blockHeight);

        for (var ty = 0; ty < tileHeight; ty++) {
            var srcIndex = ((srcTileY + ty) * srcTileWidth + srcTileX) * format.blockSize();
            var dstIndex = ((dstTileY + ty) * dstTileWidth + dstTileX) * format.blockSize();
            src.data.slice(srcIndex, tileStride).copyTo((Bytes.Mutable) dst.data, dstIndex);
        }
    }

    private static void copyCheck(Surface surface, int x, int y, int w, int h) {
        Check.fromIndexSize(x, w, surface.width);
        Check.fromIndexSize(y, h, surface.height);
        Check.argument(surface.depth == 1, "depth must be 1");
    }
}
