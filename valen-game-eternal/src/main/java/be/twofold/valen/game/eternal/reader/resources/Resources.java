package be.twofold.valen.game.eternal.reader.resources;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.hash.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Resources(
    ResourcesHeader header,
    List<ResourcesEntry> entries,
    ResourcesStrings strings,
    List<ResourcesDependency> dependencies,
    Ints dependencyIndex,
    Longs stringIndex,
    long hash
) {
    public static Resources read(BinaryReader reader) throws IOException {
        var header = ResourcesHeader.read(reader);

        reader.position(Math.toIntExact(header.resourceEntriesOffset()));
        var entries = reader.readObjects(header.numResources(), ResourcesEntry::read);

        reader.position(Math.toIntExact(header.stringTableOffset()));
        var strings = ResourcesStrings.read(reader);

        reader.position(Math.toIntExact(header.resourceDepsOffset()));
        var dependencies = reader.readObjects(header.numDependencies(), ResourcesDependency::read);
        var dependencyIndex = reader.readInts(header.numDepIndices());
        var stringIndex = reader.readLongs(header.numStringIndices());

        var end = reader.position();
        reader.position(Math.toIntExact(header.resourceEntriesOffset()));
        var toHash = reader.readBytes(Math.toIntExact(end - reader.position() + 4));
        var hash = HashFunction.farmHashFingerprint64().hash(toHash).asLong();

        return new Resources(
            header,
            entries,
            strings,
            dependencies,
            dependencyIndex,
            stringIndex,
            hash
        );
    }
}
