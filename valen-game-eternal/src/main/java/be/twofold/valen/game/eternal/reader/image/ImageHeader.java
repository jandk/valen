package be.twofold.valen.game.eternal.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;

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
    public static final int BYTES = 63;

    public static ImageHeader read(DataSource source) throws IOException {
        source.expectByte((byte) 'B');
        source.expectByte((byte) 'I');
        source.expectByte((byte) 'M');
        var version = source.readByte();

        var textureType = ImageTextureType.fromCode(source.readInt());
        var textureMaterialKind = ImageTextureMaterialKind.fromCode(source.readInt());
        var pixelWidth = source.readInt();
        var pixelHeight = source.readInt();
        var depth = source.readInt();
        var mipCount = source.readInt();
        var unkFloat1 = source.readFloat();
        var unkFloat2 = source.readFloat();
        var unkFloat3 = source.readFloat();
        source.expectByte((byte) 0);
        var textureFormat = ImageTextureFormat.fromCode(source.readInt());
        source.expectInt(7); // always 7
        source.expectInt(0); // padding
        source.expectShort((short) 0); // padding
        var streamed = source.readBoolByte();
        var singleStream = source.readBoolByte();
        var noMips = source.readBoolByte();
        var fftBloom = source.readBoolByte();
        var streamDBMipCount = source.readInt();

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
