package be.twofold.valen.core.texture;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;
import java.util.function.*;

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

    public Texture toTexture() {
        TextureKind kind = depth > 1 ? TextureKind.TEXTURE_3D : TextureKind.TEXTURE_2D;
        return new Texture(format, kind, width, height, depth, List.of(this), UnaryOperator.identity());
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
            src.data.slice(srcIndex, tileStride).copyTo((Bytes.Mutable) dst.data, dstIndex);
        }
    }
}
