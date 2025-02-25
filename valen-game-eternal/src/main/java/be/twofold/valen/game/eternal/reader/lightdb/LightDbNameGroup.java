package be.twofold.valen.game.eternal.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record LightDbNameGroup(
    int id,
    List<String> names
) {
    public static LightDbNameGroup read(DataSource source) throws IOException {
        int id = source.readInt();
        var names = source.readObjects(source.readInt(), DataSource::readPString);
        return new LightDbNameGroup(id, names);
    }
}
