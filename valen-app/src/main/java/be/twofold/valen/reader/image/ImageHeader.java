package be.twofold.valen.reader.image;

import be.twofold.valen.core.util.*;

public record ImageHeader(
    byte version,
    ImageTextureType textureType,
    ImageTextureMaterialKind textureMaterialKind,
    int pixelWidth,
    int pixelHeight,
    int depth,
    int mipCount,
    float unkFloat1,
    float unkFloat2,
    float unkFloat3,
    ImageTextureFormat textureFormat,
    boolean streamed,
    boolean singleStream,
    boolean noMips,
    boolean fftBloom,
    int streamDBMipCount
) {
    static final int BYTES = 63;

    public static ImageHeader read(BetterBuffer buffer) {
        buffer.expectByte('B');
        buffer.expectByte('I');
        buffer.expectByte('M');
        var version = buffer.getByte();

        var textureType = ImageTextureType.fromCode(buffer.getInt());
        var textureMaterialKind = ImageTextureMaterialKind.fromCode(buffer.getInt());
        var pixelWidth = buffer.getInt();
        var pixelHeight = buffer.getInt();
        var depth = buffer.getInt();
        var mipCount = buffer.getInt();
        var unkFloat1 = buffer.getFloat();
        var unkFloat2 = buffer.getFloat();
        var unkFloat3 = buffer.getFloat();
        buffer.expectByte(0);
        var textureFormat = ImageTextureFormat.fromCode(buffer.getInt());
        buffer.expectInt(7); // always 7
        buffer.expectInt(0); // padding
        buffer.expectShort(0); // padding
        var streamed = buffer.getByteAsBool();
        var singleStream = buffer.getByteAsBool();
        var noMips = buffer.getByteAsBool();
        var fftBloom = buffer.getByteAsBool();
        var streamDBMipCount = buffer.getInt();

        return new ImageHeader(
            version,
            textureType,
            textureMaterialKind,
            pixelWidth,
            pixelHeight,
            depth,
            mipCount,
            unkFloat1,
            unkFloat2,
            unkFloat3,
            textureFormat,
            streamed,
            singleStream,
            noMips,
            fftBloom,
            streamDBMipCount
        );
    }

    public int startMip() {
        var mask = switch (textureType) {
            case TT_2D -> 0x0f;
            case TT_CUBIC -> 0xff;
            default -> throw new IllegalArgumentException("Unsupported texture type: " + textureType);
        };
        return streamDBMipCount & mask;
    }

    public int totalMipCount() {
        var faces = switch (textureType) {
            case TT_2D -> 1;
            case TT_CUBIC -> 6;
            default -> throw new IllegalArgumentException("Unsupported texture type: " + textureType);
        };
        return mipCount * faces;
    }
}
