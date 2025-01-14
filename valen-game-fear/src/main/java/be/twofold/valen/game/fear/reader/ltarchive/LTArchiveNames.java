package be.twofold.valen.game.fear.reader.ltarchive;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record LTArchiveNames(
    Map<Integer, String> names
) {
    public LTArchiveNames {
        names = Map.copyOf(names);
    }

    public static LTArchiveNames read(DataSource source, int size) throws IOException {
        var start = source.tell();
        var names = new HashMap<Integer, String>();
        while (true) {
            var position = source.tell() - start;
            if (position >= size) {
                break;
            }

            var value = source.readCString();
            // Set position to multiple of 4
            source.skip(3 - ((source.tell() + 3) & 3));
            names.put(Math.toIntExact(position), value);
        }
        return new LTArchiveNames(names);
    }
}
