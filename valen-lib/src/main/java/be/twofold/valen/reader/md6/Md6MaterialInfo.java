package be.twofold.valen.reader.md6;

import be.twofold.valen.core.util.*;

public record Md6MaterialInfo(String name, int meshId, int vertexFrom, int vertexTo) {
    public static Md6MaterialInfo read(BetterBuffer buffer) {
        var name = buffer.getString();
        var meshId = buffer.getInt();
        var vertexFrom = buffer.getInt();
        var vertexTo = buffer.getInt();
        return new Md6MaterialInfo(name, meshId, vertexFrom, vertexTo);
    }
}
