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
    public static final int BYTES = 16;
    public static final int COVER_SIZE = 2048;

    public static Mega2PageHeader read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);

        byte qualityDiffuse = reader.readByte();
        byte qualitySpecular = reader.readByte();
        byte qualityLightmap = reader.readByte();
        byte flags = reader.readByte();
        short diffuseSize = reader.readShort();
        short specularSize = reader.readShort();
        short lightmapSize = reader.readShort();
        short colormaskSize = reader.readShort();
        short unusedSize = reader.readShort();
        byte zeroFlags = reader.readByte();
        byte coverFill = reader.readByte();

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

    // The flag marks the cover missing, so a clear bit means present.
    public boolean hasCover() {
        return (zeroFlags & 0x20) == 0;
    }

    public int offset(int layer) {
        var offset = 0;
        for (var before = 0; before < layer; before++) {
            offset += size(before);
        }
        return offset;
    }

    public int size(int layer) {
        return switch (layer) {
            case 0 -> Short.toUnsignedInt(diffuseSize);
            case 1 -> Short.toUnsignedInt(specularSize);
            case 2 -> Short.toUnsignedInt(lightmapSize);
            case 3 -> Short.toUnsignedInt(colormaskSize);
            case 4 -> hasCover() ? COVER_SIZE : 0;
            default -> throw new IllegalArgumentException("Invalid layer");
        };
    }
}
