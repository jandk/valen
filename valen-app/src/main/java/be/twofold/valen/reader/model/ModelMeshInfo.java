package be.twofold.valen.reader.model;

import be.twofold.valen.core.util.*;

public record ModelMeshInfo(
    String decl,
    int unkHash,
    int unknown
) {
    public static ModelMeshInfo read(BetterBuffer buffer) {
        String decl = buffer.getString();
        int unkHash = buffer.getInt();
        int unknown = buffer.getInt();
        buffer.expectInt(0);

        return new ModelMeshInfo(decl, unkHash, unknown);
    }
}
