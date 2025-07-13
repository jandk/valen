package be.twofold.valen.game.eternal.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record LightDbNameGroup(
    int id,
    List<String> names
) {
    public static LightDbNameGroup read(BinaryReader reader) throws IOException {
        int id = reader.readInt();
        var names = reader.readObjects(reader.readInt(), BinaryReader::readPString);
        return new LightDbNameGroup(id, names);
    }
}
