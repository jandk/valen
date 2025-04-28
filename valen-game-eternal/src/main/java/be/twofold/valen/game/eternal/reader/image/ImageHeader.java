package be.twofold.valen.game.eternal.reader.image;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.defines.*;
import be.twofold.valen.game.idtech.defines.*;

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
    int streamDBMipCount
) {
    public static ImageHeader read(DataSource source) throws IOException {
        source.expectByte((byte) 'B');
        source.expectByte((byte) 'I');
        source.expectByte((byte) 'M');
        var version = source.readByte();

        var textureType = TextureType.fromValue(source.readInt());
        var textureMaterialKind = TextureMaterialKind.fromValue(source.readInt());
        var pixelWidth = source.readInt();
        var pixelHeight = source.readInt();
        var depth = source.readInt();
        var mipCount = source.readInt();
        var unkFlags = source.readInt();
        var albedoSpecularBias = source.readFloat();
        var albedoSpecularScale = source.readFloat();
        source.expectByte((byte) 0);
        var textureFormat = TextureFormat.fromValue(source.readInt());
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
            unkFlags,
            albedoSpecularBias,
            albedoSpecularScale,
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
