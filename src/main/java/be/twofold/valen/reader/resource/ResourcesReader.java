package be.twofold.valen.reader.resource;

import be.twofold.valen.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public final class ResourcesReader {
    private final SeekableByteChannel channel;

    private ResourcesHeader header;
    private List<ResourcesDependency> dependencies;
    private int[] dependencyIndexes;

    public ResourcesReader(SeekableByteChannel channel) {
        this.channel = channel;
    }

    public Resources read(boolean withDeps) throws IOException {
        header = IOUtils.readStruct(channel, ResourcesHeader.Size, ResourcesHeader::read);
        List<ResourcesEntry> entries = IOUtils.readStructs(channel, header.numFileEntries(), ResourcesEntry.Size, ResourcesEntry::read);
        List<String> pathStrings = readPathStrings();

        readDependencies(withDeps);

        long[] pathStringIndexes = IOUtils.readLongs(channel, header.numPathStringIndexes());
        List<String> strings = Arrays.stream(pathStringIndexes)
            .mapToInt(Math::toIntExact)
            .mapToObj(pathStrings::get)
            .toList();

        if (channel.position() != header.addrEndMarker()) {
            throw new IOException("Expected to be at " + header.addrEndMarker() + " but was at " + channel.position());
        }

        return new Resources(header, entries, strings, dependencies, dependencyIndexes);
    }

    private void readDependencies(boolean withDeps) throws IOException {
        if (withDeps) {
            dependencies = IOUtils.readStructs(channel, header.numDependencyEntries(), ResourcesDependency.Size, ResourcesDependency::read);
            dependencyIndexes = IOUtils.readInts(channel, header.numDependencyIndexes());
        } else {
            channel.position(header.addrDependencyIndexes() + header.numDependencyIndexes() * 4L);
        }
    }

    private List<String> readPathStrings() throws IOException {
        int numStrings = Math.toIntExact(IOUtils.readBuffer(channel, 8).getLong());
        long[] stringOffsets = IOUtils.readLongs(channel, numStrings);

        int bufferLength = (int) (header.addrDependencyEntries() - channel.position());
        String buffer = IOUtils.readString(channel, bufferLength);

        return Arrays.stream(stringOffsets)
            .mapToInt(Math::toIntExact)
            .mapToObj(i -> buffer.substring(i, buffer.indexOf('\0', i)))
            .toList();
    }
}
