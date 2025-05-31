package be.twofold.valen.game.greatcircle.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StreamDb(
    StreamDbHeader header,
    long[] identities,
    List<StreamDbEntry> entries
) {
    public static StreamDb read(DataSource source) throws IOException {
        var header = StreamDbHeader.read(source);
        var identities = source.readLongs(header.numEntries());
        var entries = source.readObjects(header.numEntries(), StreamDbEntry::read);

        return new StreamDb(
            header,
            identities,
            entries
        );
    }
}
