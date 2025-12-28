package be.twofold.valen.game.eternal.reader.lightdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record LightDbNameGroup(
    int id,
    List<String> names
) {
    public static LightDbNameGroup read(BinarySource source) throws IOException {
        int id = source.readInt();
        var names = source.readStrings(source.readInt(), StringFormat.INT_LENGTH);
        return new LightDbNameGroup(id, names);
    }
}
