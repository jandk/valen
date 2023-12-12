package be.twofold.valen.reader.resource;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public record Resources(
    ResourcesHeader header,
    List<ResourcesEntry> entries,
    List<String> pathStrings,
    int[] pathStringIndex,
    List<ResourcesDependency> dependencies,
    int[] dependencyIndex
) {
    private static final CharsetDecoder DECODER = StandardCharsets.US_ASCII.newDecoder();

    public static Resources read(SeekableByteChannel channel) throws IOException {
        // Header
        var header = IOUtils.readStruct(channel, ResourcesHeader.BYTES, ResourcesHeader::read);

        // File Entries
        assert channel.position() == header.addrFileEntries();
        var entries = IOUtils.readStructs(channel, header.numFileEntries(), ResourcesEntry.BYTES, ResourcesEntry::read);

        // Path Strings
        assert channel.position() == header.addrPathStringOffsets();
        var numStrings = IOUtils.readBuffer(channel, 8).getLongAsInt();
        var offsets = IOUtils.readLongsAsInts(channel, numStrings);
        var stringBufferLength = Math.toIntExact(header.addrDependencyEntries() - channel.position());
        var stringBufferRaw = IOUtils.readBytes(channel, stringBufferLength);
        var stringBuffer = DECODER.decode(ByteBuffer.wrap(stringBufferRaw)).toString();
        var pathStrings = Arrays.stream(offsets)
            .mapToObj(i -> stringBuffer.substring(i, stringBuffer.indexOf('\0', i)))
            .toList();

        // Dependencies
        // String indices are stored in the dependency chunk for some reason...
        // My guess is that the actual filenames don't matter, and the dependency structure is used to determine
        // which files to load. The filenames are only used for debugging purposes.
        assert channel.position() == header.addrDependencyEntries();
        var dependencies = IOUtils.readStructs(channel, header.numDependencyEntries(), ResourcesDependency.BYTES, ResourcesDependency::read);
        var dependencyIndex = IOUtils.readInts(channel, header.numDependencyIndexes());
        var pathStringIndex = IOUtils.readLongsAsInts(channel, header.numPathStringIndexes());

        assert channel.position() == header.addrEndMarker();
        return new Resources(header, entries, pathStrings, pathStringIndex, dependencies, dependencyIndex);
    }

    @Override
    public String toString() {
        return "Resources[" +
            "header=" + header + ", " +
            "entries=(" + entries.size() + " entries), " +
            "pathStrings=(" + pathStrings.size() + " strings), " +
            "pathStringIndex=(" + pathStringIndex.length + " indices), " +
            "dependencies=(" + dependencies.size() + " entries), " +
            "dependencyIndex=(" + dependencyIndex.length + " indices)" +
            "]";
    }
}
