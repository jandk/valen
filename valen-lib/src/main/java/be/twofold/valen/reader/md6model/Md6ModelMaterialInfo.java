package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.util.*;

public record Md6ModelMaterialInfo(String name, int meshId, int vertexFrom, int vertexTo) {
    public static Md6ModelMaterialInfo read(BetterBuffer buffer) {
        var name = buffer.getString();
        var meshId = buffer.getInt();
        var vertexFrom = buffer.getInt();
        var vertexTo = buffer.getInt();
        return new Md6ModelMaterialInfo(name, meshId, vertexFrom, vertexTo);
    }
}
