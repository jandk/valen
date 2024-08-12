package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record LightDbNameGroup(
    int id,
    List<String> names
) {
    public static LightDbNameGroup read(DataSource source) throws IOException {
        int id = source.readInt();
        var names = source.readStructs(source.readInt(), DataSource::readPString);
        return new LightDbNameGroup(id, names);
    }
}
