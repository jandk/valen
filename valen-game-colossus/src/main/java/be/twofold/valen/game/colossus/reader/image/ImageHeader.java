package be.twofold.valen.game.colossus.reader.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ImageHeader(
    int magic,
    ImageTextureType textureType,
    ImageTextureMaterialKind textureMaterialKind,
    int pixelWidth,
    int pixelHeight,
    int depth,
    int mipCount,
    byte padding0,
    ImageTextureFormat textureFormat,
    int always5,
    int padding,
    byte padding1,
    byte padding2,
    byte padding3,
    byte unkBool1,
    byte unkBool2,
    int streamMipCount
) {
    public static ImageHeader read(BinarySource source) throws IOException {
        var magic = source.readInt();
        var textureType = source.readInt();
        var textureMaterialKind = source.readInt();
        var pixelWidth = source.readInt();
        var pixelHeight = source.readInt();
        var depth = source.readInt();
        var mipCount = source.readInt();
        var padding0 = source.readByte();
        var textureFormat = source.readInt();
        var always5 = source.readInt();
        var padding = source.readInt();
        var padding1 = source.readByte();
        var padding2 = source.readByte();
        var padding3 = source.readByte();
        var unkBool1 = source.readByte();
        var unkBool2 = source.readByte();
        var streamMipCount = source.readInt();

        return new ImageHeader(
            magic,
            ImageTextureType.fromCode(textureType),
            ImageTextureMaterialKind.fromCode(textureMaterialKind),
            pixelWidth,
            pixelHeight,
            depth,
            mipCount,
            padding0,
            ImageTextureFormat.fromCode(textureFormat),
            always5,
            padding,
            padding1,
            padding2,
            padding3,
            unkBool1,
            unkBool2,
            streamMipCount
        );
    }

    public int startMip() {
        var mask = switch (textureType) {
            case TT_2D -> 0x0f;
            case TT_CUBIC -> 0xff;
            default -> throw new IllegalArgumentException("Unsupported texture type: " + textureType);
        };
        return streamMipCount & mask;
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
