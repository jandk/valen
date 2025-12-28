package be.twofold.valen.game.colossus.reader.texdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record TexDb(
    TexDbHeader header,
    List<TexDbEntry> entries
) {
    public static TexDb read(BinarySource source) throws IOException {
        var header = TexDbHeader.read(source);
        var entries = source.readObjects(header.numEntries(), TexDbEntry::read);

        return new TexDb(
            header,
            entries
        );
    }
}
