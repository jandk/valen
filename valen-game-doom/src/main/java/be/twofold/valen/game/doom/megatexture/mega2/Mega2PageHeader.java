package be.twofold.valen.game.doom.megatexture.mega2;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public record Mega2PageHeader(
    byte qualityDiffuse,
    byte qualitySpecular,
    byte qualityLightmap,
    byte flags,
    short diffuseSize,
    short specularSize,
    short lightmapSize,
    short colormaskSize,
    short unusedSize,
    byte zeroFlags,
    byte coverFill
) {
    public static Mega2PageHeader read(BinarySource source) throws IOException {
        source.order(ByteOrder.BIG_ENDIAN);

        byte qualityDiffuse = source.readByte();
        byte qualitySpecular = source.readByte();
        byte qualityLightmap = source.readByte();
        byte flags = source.readByte();
        short diffuseSize = source.readShort();
        short specularSize = source.readShort();
        short lightmapSize = source.readShort();
        short colormaskSize = source.readShort();
        short unusedSize = source.readShort();
        byte zeroFlags = source.readByte();
        byte coverFill = source.readByte();

        return new Mega2PageHeader(
            qualityDiffuse,
            qualitySpecular,
            qualityLightmap,
            flags,
            diffuseSize,
            specularSize,
            lightmapSize,
            colormaskSize,
            unusedSize,
            zeroFlags,
            coverFill
        );
    }
}
