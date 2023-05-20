package be.twofold.valen.reader.resource;

import be.twofold.valen.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public final class ResourcesReader {
    private final SeekableByteChannel channel;

    private ResourcesHeader header;
    private List<String> strings;
    private int[] stringIndexes;
    private List<ResourcesDependency> dependencies;
    private int[] dependencyIndexes;

    public ResourcesReader(SeekableByteChannel channel) {
        this.channel = channel;
    }

    public Resources read(boolean withDeps) throws IOException {
        header = IOUtils.readStruct(channel, ResourcesHeader.Size, ResourcesHeader::read);
        readStringChunk();
        readDependencyChunk(withDeps);
        assert channel.position() == header.addrEndMarker() : "We're in the wrong place!";

        List<ResourcesEntry> entries = readEntryChunk();
        return new Resources(header, entries, dependencies, dependencyIndexes);
    }

    private void readStringChunk() throws IOException {
        channel.position(header.addrPathStringOffsets());
        int numStrings = Math.toIntExact(IOUtils.readBuffer(channel, 8).getLong());
        long[] stringOffsets = IOUtils.readLongs(channel, numStrings);

        int bufferLength = (int) (header.addrDependencyEntries() - channel.position());
        String buffer = IOUtils.readString(channel, bufferLength);

        strings = Arrays.stream(stringOffsets)
            .mapToInt(Math::toIntExact)
            .mapToObj(i -> buffer.substring(i, buffer.indexOf('\0', i)))
            .toList();
    }

    private void readDependencyChunk(boolean withDeps) throws IOException {
        channel.position(header.addrDependencyIndexes());
        if (withDeps) {
            dependencies = IOUtils.readStructs(channel, header.numDependencyEntries(), ResourcesDependency.Size,
                buffer -> ResourcesDependency.read(buffer, strings));
            dependencyIndexes = IOUtils.readInts(channel, header.numDependencyIndexes());
        } else {
            channel.position(channel.position() + header.numDependencyIndexes() * 4L);
        }

        // String indices are stored in the dependency chunk for some reason...
        // My guess is that the actual filenames don't matter, and the dependency structure is used to determine
        // which files to load. The filenames are only used for debugging purposes.
        stringIndexes = Arrays.stream(IOUtils.readLongs(channel, header.numPathStringIndexes()))
            .mapToInt(Math::toIntExact)
            .toArray();
    }

    private List<ResourcesEntry> readEntryChunk() throws IOException {
        // This position is stored in the file somewhere, but the value is always 124
        channel.position(ResourcesHeader.Size);
        return IOUtils.readStructs(channel, header.numFileEntries(), ResourcesEntry.Size,
            buffer -> ResourcesEntry.read(buffer, stringIndexes, strings));
    }
}
