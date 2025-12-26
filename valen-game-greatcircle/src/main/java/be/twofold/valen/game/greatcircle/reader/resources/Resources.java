package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public record Resources(
    ResourcesHeader header,
    List<ResourcesEntry> entries,
    List<String> pathStrings,
    Ints pathStringIndex,
    List<ResourcesDependency> dependencies,
    Ints dependencyIndex,
    List<ResourceSpecialHash> specialHashes
) {
    private static final CharsetDecoder DECODER = StandardCharsets.US_ASCII.newDecoder();

    public static Resources read(BinarySource source) throws IOException {
        // Header
        var header = ResourcesHeader.read(source);

        // File Entries
        // assert channel.position() == header.addrFileEntries();
        var entries = source.readObjects(header.numFileEntries(), ResourcesEntry::read);

        // Path Strings
        // assert channel.position() == header.addrPathStringOffsets();
        var numStrings = source.readLongAsInt();
        var offsets = source.readLongsAsInts(numStrings);
        var stringBufferLength = header.addrDependencyEntries() - header.addrPathStringOffsets() - (numStrings + 1) * Long.BYTES;
        var stringBufferRaw = source.readBytes(stringBufferLength);
        var stringBuffer = DECODER.decode(stringBufferRaw.asBuffer()).toString();
        var pathStrings = offsets.stream()
            .mapToObj(i -> stringBuffer.substring(i, stringBuffer.indexOf('\0', i)))
            .toList();

        // Dependencies
        // String indices are stored in the dependency chunk for some reason...
        // My guess is that the actual filenames don't matter, and the dependency structure is used to determine
        // which files to load. The filenames are only used for debugging purposes.
        // assert channel.position() == header.addrDependencyEntries();
        var dependencies = source.readObjects(header.numDependencyEntries(), ResourcesDependency::read);
        var specialHashes = source.readObjects(header.numSpecialHashes(), ResourceSpecialHash::read);
        var dependencyIndex = source.readInts(header.numDependencyIndexes());
        var pathStringIndex = source.readLongsAsInts(header.numPathStringIndexes());

        // assert channel.position() == header.addrEndMarker();
        return new Resources(header, entries, pathStrings, pathStringIndex, dependencies, dependencyIndex, specialHashes);
    }

    @Override
    public String toString() {
        return "Resources[" +
            "header=" + header + ", " +
            "entries=(" + entries.size() + " entries), " +
            "pathStrings=(" + pathStrings.size() + " strings), " +
            "pathStringIndex=(" + pathStringIndex.length() + " indices), " +
            "dependencies=(" + dependencies.size() + " entries), " +
            "dependencyIndex=(" + dependencyIndex.length() + " indices)" +
            "]";
    }
}
