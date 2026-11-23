package be.twofold.valen.game.greatcircle.reader.streamdb;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StreamDb(
    StreamDbHeader header,
    Longs identities,
    List<StreamDbEntry> entries
) {
    public static StreamDb read(BinaryReader reader) throws IOException {
        var header = StreamDbHeader.read(reader);
        var identities = reader.readLongs(header.numEntries());
        var entries = reader.readObjects(header.numEntries(), StreamDbEntry::read);

        return new StreamDb(
            header,
            identities,
            entries
        );
    }
}
