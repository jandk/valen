package be.twofold.valen.game.eternal.reader.lightdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record LightDbNameGroup(
    int id,
    List<String> names
) {
    public static LightDbNameGroup read(BinaryReader reader) throws IOException {
        int id = reader.readInt();
        var names = reader.readStrings(reader.readInt(), StringFormat.INT_LENGTH);
        return new LightDbNameGroup(id, names);
    }
}
