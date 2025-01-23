package be.twofold.valen.game.doom.resources;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record ResourcesIndex(
    ResourcesIndexHeader header,
    List<ResourcesIndexEntry> entries
) {
    public static ResourcesIndex read(Path path) throws IOException {
        try (var source = DataSource.fromPath(path)) {
            var header = ResourcesIndexHeader.read(source);
            if (header.version() != 5) {
                throw new IOException("Unsupported version: " + header.version());
            }
            var entries = source.readStructs(header.count(), ResourcesIndexEntry::read);

            return new ResourcesIndex(header, entries);
        }
    }

    @Override
    public String toString() {
        return "ResourcesIndex(" +
            "header=" + header + ", " +
            "entries=[" + entries.size() + "]" +
            ")";
    }
}
