package be.twofold.valen.reader.md6;

import be.twofold.valen.core.util.*;

public record Md6MaterialInfo(String name, int meshId, int vertexFrom, int vertexTo) {
    public static Md6MaterialInfo read(BetterBuffer buffer) {
        String name = buffer.getString();
        int meshId = buffer.getInt();
        int vertexFrom = buffer.getInt();
        int vertexTo = buffer.getInt();
        return new Md6MaterialInfo(name, meshId, vertexFrom, vertexTo);
    }
}
