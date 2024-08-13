package be.twofold.valen.game.doom.resources;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record ResourcesIndex(
    ResourcesIndexHeader header,
    List<ResourcesIndexEntry> entries
) {
    public static ResourcesIndex read(DataSource source) throws IOException {
        var header = ResourcesIndexHeader.read(source);
        if (header.version() != 5) {
            throw new IOException("Unsupported version: " + header.version());
        }

        var numEntries = Integer.reverseBytes(source.readInt());
        var entries = source.readStructs(numEntries, ResourcesIndexEntry::read);

        return new ResourcesIndex(header, entries);
    }

    @Override
    public String toString() {
        return "ResourcesIndex(" +
            "header=" + header + ", " +
            "entries=[" + entries.size() + "]" +
            ")";
    }
}
