package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class PsBin {

    public static HashMap<String, Object> read(DataSource source) throws IOException {
        var count = source.readInt();
        var map = new HashMap<String, Object>(count);
        for (int i = 0; i < count; i++) {
            var name = source.readPString();
            var bin = readValue(source);
            map.put(name, bin);
        }
        return map;
    }

    private static Object readValue(DataSource source) throws IOException {
        var type = source.readInt();
        return switch (type) {
            case 1 -> source.readInt();
            case 2 -> source.readFloat();
            case 3 -> source.readBoolByte();
            case 4 -> source.readPString();
            case 6 -> {
                var count = source.readInt();
                var arr = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    arr.add(readValue(source));
                }
                yield arr;
            }
            case 7 -> PsBin.read(source);
            default -> throw new IOException("Unimplemented type: " + type);
        };
    }
}
