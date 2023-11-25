package be.twofold.valen.reader.model;

import be.twofold.valen.*;

public record ModelMeshInfo(
    String decl,
    int unknown3,
    int dummy1
) {
    public static ModelMeshInfo read(BetterBuffer buffer) {
        String decl = buffer.getString();
        int unkHash = buffer.getInt();
        int unknown = buffer.getInt();
        buffer.expectInt(0);

        return new ModelMeshInfo(decl, unkHash, unknown);
    }
}
