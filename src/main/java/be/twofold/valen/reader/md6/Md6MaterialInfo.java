package be.twofold.valen.reader.md6;

import be.twofold.valen.*;

public record Md6MaterialInfo(String name, int unknown1, int unknown2, int unknown3) {
    public static Md6MaterialInfo read(BetterBuffer buffer) {
        String name = buffer.getString();
        int unknown1 = buffer.getInt();
        int unknown2 = buffer.getInt();
        int unknown3 = buffer.getInt();
        return new Md6MaterialInfo(name, unknown1, unknown2, unknown3);
    }
}
