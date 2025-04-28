package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.greatcircle.defines.*;
import be.twofold.valen.game.idtech.defines.*;

import java.io.*;

public record ImageHeader(
    int magic,
    TextureType type,
    TextureMaterialKind kind,
    int width,
    int height,
    int depth,
    int count,
    int mipCount,
    int unknown1,
    float bias,
    float scale,
    TextureFormat textureFormat,
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

        var type = TextureType.fromValue(source.readInt());
        var kind = TextureMaterialKind.fromValue(source.readInt());
        var width = source.readInt();
        var height = source.readInt();
        var depth = source.readInt();
        var count = version < 31 ? 1 : source.readInt();
        var mipCount = source.readInt();
        var unknown1 = source.readInt();
        var bias = source.readFloat();
        var scale = source.readFloat();
        source.expectByte((byte) 0);
        var textureFormat = TextureFormat.fromValue(source.readInt());
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
            bias,
            scale,
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
