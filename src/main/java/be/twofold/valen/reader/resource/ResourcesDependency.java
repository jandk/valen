package be.twofold.valen.reader.resource;

import java.nio.*;

public record ResourcesDependency(
    int assetTypeStringIndex,
    int fileNameStringIndex,
    int dependencyType,
    int unk18,
    int unk1c
) {
    static final int Size = 0x20;

    public static ResourcesDependency read(ByteBuffer buffer) {
        int assetTypeStringIndex = Math.toIntExact(buffer.getLong(0x00));
        int fileNameStringIndex = Math.toIntExact(buffer.getLong(0x08));
        int dependencyType = buffer.getInt(0x10);
        int unk18 = buffer.getInt(0x18);
        int unk1C = buffer.getInt(0x1c);

        return new ResourcesDependency(
            assetTypeStringIndex,
            fileNameStringIndex,
            dependencyType,
            unk18,
            unk1C
        );
    }
}
