package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.util.*;

import java.util.*;

public record LightDbNameGroup(
    int id,
    List<String> names
) {
    public static LightDbNameGroup read(BetterBuffer buffer) {
        int id = buffer.getInt();
        int nameCount = buffer.getInt();
        var names = new ArrayList<String>();
        for (int i = 0; i < nameCount; i++) {
            names.add(buffer.getString());
        }
        return new LightDbNameGroup(id, names);
    }
}
