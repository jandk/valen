package be.twofold.valen.reader.model;

import be.twofold.valen.core.util.*;

public record ModelHeader(
    int numMeshes,
    float unknown1,
    float unknown2,
    float unknown3,
    boolean streamed
) {
    public static ModelHeader read(BetterBuffer buffer) {
        buffer.expectInt(0);
        buffer.expectInt(0);
        buffer.expectInt(0);
        buffer.expectInt(5);
        var numMeshes = buffer.getInt();
        buffer.expectInt(0);
        var unknown1 = buffer.getFloat();
        var unknown2 = buffer.getFloat();
        var unknown3 = buffer.getFloat();
        buffer.expectInt(0);
        var streamed = buffer.getIntAsBool();
        return new ModelHeader(
            numMeshes,
            unknown1,
            unknown2,
            unknown3,
            streamed
        );
    }
}
