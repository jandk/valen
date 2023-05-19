package be.twofold.valen.reader.resource;

import java.nio.*;
import java.util.*;

public record ResourcesDependency(
    String typeName,
    String fileName,
    int dependencyType,
    int unk18,
    int unk1c
) {
    static final int Size = 0x20;

    public static ResourcesDependency read(ByteBuffer buffer, List<String> strings) {
        String typeName = strings.get(Math.toIntExact(buffer.getLong(0x00)));
        String fileName = strings.get(Math.toIntExact(buffer.getLong(0x08)));
        int dependencyType = buffer.getInt(0x10);
        int unk18 = buffer.getInt(0x18);
        int unk1c = buffer.getInt(0x1c);

        return new ResourcesDependency(
            typeName,
            fileName,
            dependencyType,
            unk18,
            unk1c
        );
    }
}
