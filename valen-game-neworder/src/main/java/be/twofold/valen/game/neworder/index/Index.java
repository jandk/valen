package be.twofold.valen.game.neworder.index;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record Index(
    IndexHeader header,
    List<IndexEntry> entries
) {
    public static Index read(Path path) throws IOException {
        try (var source = DataSource.fromPath(path)) {
            var header = IndexHeader.read(source);
            var entries = source.readStructs(header.count(), IndexEntry::read);
            source.expectEnd();

            return new Index(header, entries);
        }
    }

    @Override
    public String toString() {
        return "Index(header=" + header + ", entries=[" + entries.size() + " entries])";
    }
}
