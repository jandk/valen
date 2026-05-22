package be.twofold.valen.game.doom.mega2;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public record Mega2PageHeader(
    byte qualityDiffuse,
    byte qualitySpecular,
    byte qualityLightmap,
    byte qualityColorMask,
    short diffuseSize,
    short specularSize,
    short lightmapSize,
    short colorMaskSize,
    short unknownSize,
    byte flags,
    byte reserved
) {
    public static Mega2PageHeader read(BinarySource source) throws IOException {
        source.order(ByteOrder.BIG_ENDIAN);

        byte qualityDiffuse = source.readByte();
        byte qualitySpecular = source.readByte();
        byte qualityLightmap = source.readByte();
        byte qualityColorMask = source.readByte();
        short diffuseSize = source.readShort();
        short specularSize = source.readShort();
        short lightmapSize = source.readShort();
        short colorMaskSize = source.readShort();
        short unknownSize = source.readShort();
        byte flags = source.readByte();
        byte reserved = source.readByte();

        return new Mega2PageHeader(
            qualityDiffuse,
            qualitySpecular,
            qualityLightmap,
            qualityColorMask,
            diffuseSize,
            specularSize,
            lightmapSize,
            colorMaskSize,
            unknownSize,
            flags,
            reserved
        );
    }
}
