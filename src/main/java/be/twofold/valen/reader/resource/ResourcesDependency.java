package be.twofold.valen.reader.resource;

import be.twofold.valen.*;

import java.util.*;

public record ResourcesDependency(
    String typeName,
    String fileName,
    int dependencyType,
    int unk18,
    int unk1c
) {
    static final int Size = 0x20;

    public static ResourcesDependency read(BetterBuffer buffer, List<String> strings) {
        String typeName = strings.get(buffer.getLongAsInt());
        String fileName = strings.get(buffer.getLongAsInt());
        int dependencyType = buffer.getInt();
        buffer.expectInt(1);
        int unk18 = buffer.getInt();
        int unk1c = buffer.getInt();

        return new ResourcesDependency(
            typeName,
            fileName,
            dependencyType,
            unk18,
            unk1c
        );
    }
}
