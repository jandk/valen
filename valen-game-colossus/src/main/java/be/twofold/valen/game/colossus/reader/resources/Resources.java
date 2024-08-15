package be.twofold.valen.game.colossus.reader.resources;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record Resources(
    ResourceDiskHeader diskHeader,
    ResourceHeader header,
    List<ResourceEntry> entries,
    List<String> strings,
    List<ResourceDependency> dependencies,
    int[] dependencyIndex,
    int[] stringIndex
) {
    public static Resources read(DataSource source) throws IOException {
        var diskHeader = ResourceDiskHeader.read(source);
        var header = ResourceHeader.read(source);

        source.expectPosition(header.resourceEntriesOffset());
        var entries = source.readStructs(header.numResources(), ResourceEntry::read);

        source.expectPosition(header.stringTableOffset());
        var numStrings = source.readLongAsInt();
        var stringOffsets = source.readLongsAsInts(numStrings);
        var strings = source.readStructs(numStrings, DataSource::readCString);

        // There's some padding here, so can't use expectPosition
        // source.expectPosition(header.resourceDepsOffset());
        source.seek(header.resourceDepsOffset());
        var dependencies = source.readStructs(header.numDependencies(), ResourceDependency::read);
        var dependencyIndex = source.readInts(header.numDepIndices());
        var stringIndex = source.readLongsAsInts(header.numStringIndices());

        return new Resources(
            diskHeader,
            header,
            entries,
            strings,
            dependencies,
            dependencyIndex,
            stringIndex
        );
    }

    @Override
    public String toString() {
        return "Resources(" +
            "diskHeader=" + diskHeader + ", " +
            "header=" + header + ", " +
            "entries=[" + entries.size() + " items], " +
            "strings=[" + strings.size() + " items], " +
            "dependencies=[" + dependencies.size() + " items], " +
            "dependencyIndex=[" + dependencyIndex.length + " items], " +
            "stringIndex=[" + stringIndex.length + " items]" +
            ")";
    }
}
