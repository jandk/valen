package be.twofold.valen.reader.resource;

import be.twofold.valen.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public final class ResourcesReader {
    private final SeekableByteChannel channel;

    private ResourcesHeader header;

    public ResourcesReader(SeekableByteChannel channel) {
        this.channel = channel;
    }

    public Resources read() throws IOException {
        header = IOUtils.readStruct(channel, ResourcesHeader.Size, ResourcesHeader::read);
        List<ResourcesEntry> entries = IOUtils.readStructs(channel, header.numFileEntries(), ResourcesEntry.Size, ResourcesEntry::read);
        List<String> pathStrings = readPathStrings();
        List<ResourcesDependency> dependencies = IOUtils.readStructs(channel, header.numDependencyEntries(), ResourcesDependency.Size, ResourcesDependency::read);
        int[] dependencyIndexes = IOUtils.readInts(channel, header.numDependencyIndexes());
        int[] pathStringIndexes = Arrays.stream(IOUtils.readLongs(channel, header.numPathStringIndexes()))
            .mapToInt(Math::toIntExact)
            .toArray();

        if (channel.position() != header.addrEndMarker()) {
            throw new IOException("Expected to be at " + header.addrEndMarker() + " but was at " + channel.position());
        }

        return new Resources(
            header,
            entries,
            pathStrings,
            dependencies,
            dependencyIndexes,
            pathStringIndexes
        );
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
