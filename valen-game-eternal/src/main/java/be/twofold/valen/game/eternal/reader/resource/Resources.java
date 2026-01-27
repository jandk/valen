package be.twofold.valen.game.eternal.reader.resource;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Resources(
    ResourcesHeader header,
    List<ResourcesEntry> entries,
    ResourcesStrings strings,
    List<ResourcesDependency> dependencies,
    Ints dependencyIndex,
    Longs stringIndex
) {
    public static Resources read(BinarySource source) throws IOException {
        var header = ResourcesHeader.read(source);

        source.position(Math.toIntExact(header.resourceEntriesOffset()));
        var entries = source.readObjects(header.numResources(), ResourcesEntry::read);

        source.position(Math.toIntExact(header.stringTableOffset()));
        var strings = ResourcesStrings.read(source);

        source.position(Math.toIntExact(header.resourceDepsOffset()));
        var dependencies = source.readObjects(header.numDependencies(), ResourcesDependency::read);
        var dependencyIndex = source.readInts(header.numDepIndices());
        var stringIndex = source.readLongs(header.numStringIndices());

        return new Resources(
            header,
            entries,
            strings,
            dependencies,
            dependencyIndex,
            stringIndex
        );
    }
}
