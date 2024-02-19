package be.twofold.valen.reader.md6;

import be.twofold.valen.core.util.*;

public record Md6MeshMaterialInfo(String name, int meshId, int vertexFrom, int vertexTo) {
    public static Md6MeshMaterialInfo read(BetterBuffer buffer) {
        var name = buffer.getString();
        var meshId = buffer.getInt();
        var vertexFrom = buffer.getInt();
        var vertexTo = buffer.getInt();
        return new Md6MeshMaterialInfo(name, meshId, vertexFrom, vertexTo);
    }
}
