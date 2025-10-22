package be.twofold.valen.game.dyinglight.reader.texture;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;

public record DLTextureHeader(
    int unk0,
    int unk1,
    float[] range0,
    float[] range1,
    float[] range2,
    float[] range3,
    int width,
    int height,
    int layers,
    PixelFormat format,
    byte unk3,
    int unk4,
    int unk5
) {
    public static DLTextureHeader read(BinaryReader reader) throws IOException {
        var ident = reader.readString(4);
        Check.state("IMGC".equals(ident), "Invalid texture ident: " + ident);
        reader.skip(4);
        var unk0 = reader.readInt();
        var unk1 = reader.readInt();
        var range0 = reader.readFloats(4);
        var range1 = reader.readFloats(4);
        var range2 = reader.readFloats(4);
        var width = reader.readShort();
        var height = reader.readShort();
        var unk2 = reader.readShort();
        var pixelFormat = PixelFormat.fromInternalFormat(reader.readByte());
        var unk3 = reader.readByte();
        var unk4 = reader.readInt();
        var unk5 = reader.readInt();
        return new DLTextureHeader(
            unk0,
            unk1,
            range0,
            range1,
            range2,
            new float[0],
            width,
            height,
            unk2,
            pixelFormat,
            unk3,
            unk4,
            unk5
        );
    }
}
