package be.twofold.valen.game.darkages.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StreamDb(
    StreamDbHeader header,
    List<StreamDbEntry> entries
) {
    public static StreamDb read(BinarySource source) throws IOException {
        var header = StreamDbHeader.read(source);
        var entries = source.readObjects(header.numEntries(), StreamDbEntry::read);

        return new StreamDb(
            header,
            entries
        );
    }
}
