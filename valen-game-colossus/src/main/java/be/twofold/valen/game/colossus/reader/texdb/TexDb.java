package be.twofold.valen.game.colossus.reader.texdb;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record TexDb(
    TexDbHeader header,
    List<TexDbEntry> entries
) {
    public static TexDb read(DataSource source) throws IOException {
        var header = TexDbHeader.read(source);
        var entries = source.readStructs(header.numEntries(), TexDbEntry::read);

        return new TexDb(
            header,
            entries
        );
    }
}
