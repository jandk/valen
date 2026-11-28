package be.twofold.valen.game.doom.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public record ResourcesIndex(
    ResourcesIndexHeader header,
    List<ResourcesIndexEntry> entries
) {
    public static ResourcesIndex read(Path path) throws IOException {
        try (var reader = BinaryReader.open(path).order(ByteOrder.BIG_ENDIAN)) {
            var header = ResourcesIndexHeader.read(reader);
            var entries = reader.readObjects(header.count(), ResourcesIndexEntry::read);

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
