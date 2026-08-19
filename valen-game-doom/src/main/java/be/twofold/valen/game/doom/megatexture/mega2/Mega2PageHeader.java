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
