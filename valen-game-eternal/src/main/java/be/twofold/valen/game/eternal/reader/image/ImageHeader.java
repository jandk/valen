package be.twofold.valen.game.eternal.reader.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ImageHeader(
    byte version,
    TextureType textureType,
    TextureMaterialKind textureMaterialKind,
    int pixelWidth,
    int pixelHeight,
    int depth,
    int mipCount,
    int unkFlags,
    float albedoSpecularBias,
    float albedoSpecularScale,
    TextureFormat textureFormat,
    boolean streamed,
    boolean singleStream,
    boolean noMips,
    boolean fftBloom,
    int streamDbMipCount
) {
    public static ImageHeader read(BinarySource source) throws IOException {
        source.expectByte((byte) 0x42); // magic0
        source.expectByte((byte) 0x49); // magic1
        source.expectByte((byte) 0x4D); // magic2
        var version = source.readByte();
        var textureType = TextureType.read(source);
        var textureMaterialKind = TextureMaterialKind.read(source);
        var pixelWidth = source.readInt();
        var pixelHeight = source.readInt();
        var depth = source.readInt();
        var mipCount = source.readInt();
        var unkFlags = source.readInt();
        var albedoSpecularBias = source.readFloat();
        var albedoSpecularScale = source.readFloat();
        source.expectByte((byte) 0x0); // padding1
        var textureFormat = TextureFormat.read(source);
        source.expectInt(0x7); // always7
        source.expectInt(0x0); // padding2
        source.expectShort((short) 0x0); // padding3
        var streamed = source.readBool(BoolFormat.BYTE);
        var singleStream = source.readBool(BoolFormat.BYTE);
        var noMips = source.readBool(BoolFormat.BYTE);
        var fftBloom = source.readBool(BoolFormat.BYTE);
        var streamDbMipCount = source.readInt();

        return new ImageHeader(
            version,
            textureType,
            textureMaterialKind,
            pixelWidth,
            pixelHeight,
            depth,
            mipCount,
            unkFlags,
            albedoSpecularBias,
            albedoSpecularScale,
            textureFormat,
            streamed,
            singleStream,
            noMips,
            fftBloom,
            streamDbMipCount
        );
    }

    int startMip() {
        var mask = switch (textureType()) {
            case TT_2D -> 0x0f;
            case TT_CUBIC -> 0xff;
            default -> throw new UnsupportedOperationException("Unsupported texture type: " + textureType());
        };
        return streamDbMipCount() & mask;
    }

    int totalMipCount() {
        var faces = switch (textureType()) {
            case TT_2D -> 1;
            case TT_CUBIC -> 6;
            default -> throw new UnsupportedOperationException("Unsupported texture type: " + textureType());
        };
        return mipCount() * faces;
    }
}
