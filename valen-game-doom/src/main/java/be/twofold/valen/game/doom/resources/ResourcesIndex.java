package be.twofold.valen.game.doom.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record ResourcesIndex(
    ResourcesIndexHeader header,
    List<ResourcesIndexEntry> entries
) {
    public static ResourcesIndex read(Path path) throws IOException {
        try (var source = BinarySource.open(path)) {
            var header = ResourcesIndexHeader.read(source);
            var entries = source.readObjects(header.count(), ResourcesIndexEntry::read);

            return new ResourcesIndex(header, entries);
        }
    }

    @Override
    public String toString() {
        return "ResourcesIndex(" +
            "header=" + header + ", " +
            "entries=[" + entries.size() + " items]" +
            ")";
    }
}
