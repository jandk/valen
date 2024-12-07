package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ImageHeader(
    int magic,
    ImageTextureType type,
    ImageTextureMaterialKind kind,
    int width,
    int height,
    int depth,
    int count,
    int mipCount,
    int unknown1,
    float unkFloat2,
    float unkFloat3,
    ImageTextureFormat textureFormat,
    boolean streamed,
    boolean singleStream,
    boolean noMips,
    boolean fftBloom,
    int startMip,
    int streamedSize,
    boolean unknown
) {
    public static ImageHeader read(DataSource source) throws IOException {
        var magic = source.readInt();
        if (magic != 0x1F4D4942 && magic != 0x1E4D4942 && magic != 0x1D4D4942) {
            throw new IOException("Invalid magic number: " + magic);
        }
        var version = magic >>> 24;

        var type = ImageTextureType.fromCode(source.readInt());
        var kind = ImageTextureMaterialKind.fromCode(source.readInt());
        var width = source.readInt();
        var height = source.readInt();
        var depth = source.readInt();
        var count = version < 31 ? 1 : source.readInt();
        var mipCount = source.readInt();
        var unknown1 = source.readInt();
        var unkFloat2 = source.readFloat();
        var unkFloat3 = source.readFloat();
        source.expectByte((byte) 0);
        var textureFormat = ImageTextureFormat.fromCode(source.readInt());
        source.expectShort((short) 0);
        var streamed = source.readBoolByte();
        var singleStream = source.readBoolByte();
        var noMips = source.readBoolByte();
        var fftBloom = source.readBoolByte();
        source.expectInt(0);
        var startMip = source.readInt();
        var streamedSize = source.readInt();
        var unknown = source.readBoolByte();

        return new ImageHeader(
            magic,
            type,
            kind,
            width,
            height,
            depth,
            count,
            mipCount,
            unknown1,
            unkFloat2,
            unkFloat3,
            textureFormat,
            streamed,
            singleStream,
            noMips,
            fftBloom,
            startMip,
            streamedSize,
            unknown
        );
    }
}
