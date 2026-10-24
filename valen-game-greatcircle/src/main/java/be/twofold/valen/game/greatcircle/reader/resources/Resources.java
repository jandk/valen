package be.twofold.valen.game.greatcircle.reader.resources;

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
    List<ResourcesSpecialHash> specialHashes,
    Ints dependencyIndex,
    Longs stringIndex,
    long hash
) {
    public static Resources read(BinarySource source) throws IOException {
        var header = ResourcesHeader.read(source);

        source.position(Math.toIntExact(header.resourceEntriesOffset()));
        var entries = source.readObjects(header.numResources(), ResourcesEntry::read);

        source.position(Math.toIntExact(header.stringTableOffset()));
        var strings = ResourcesStrings.read(source);

        source.position(Math.toIntExact(header.resourceDepsOffset()));
        var dependencies = source.readObjects(header.numDependencies(), ResourcesDependency::read);
        var specialHashes = source.readObjects(header.numSpecialHashes(), ResourcesSpecialHash::read);
        var dependencyIndex = source.readInts(header.numDepIndices());
        var stringIndex = source.readLongs(header.numStringIndices());

        var end = source.position();
        source.position(Math.toIntExact(header.resourceEntriesOffset()));
        var toHash = source.readBytes(Math.toIntExact(end - source.position() + 4));
        var hash = HashFunction.farmHashFingerprint64().hash(toHash).asLong();

        return new Resources(
            header,
            entries,
            strings,
            dependencies,
            specialHashes,
            dependencyIndex,
            stringIndex,
            hash
        );
    }
}
