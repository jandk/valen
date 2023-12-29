package be.twofold.valen.resource;

import be.twofold.valen.core.util.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.resource.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements AutoCloseable {
    private final Map<Long, Resource> entries;
    private SeekableByteChannel channel;

    public ResourcesFile(Path path) throws IOException {
        System.out.println("Loading resources: " + path);

        this.channel = Files.newByteChannel(path, StandardOpenOption.READ);
        List<Resource> resources = new ResourceMapper().map(Resources.read(channel));
        this.entries = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                Resource::hash,
                Function.identity(),
                (first, second) -> first
            ));
    }

    public Collection<Resource> getEntries() {
        return entries.values();
    }

    public Resource getEntry(long hash) {
        return entries.get(hash);
    }

    public byte[] read(long hash) {
        var entry = entries.get(hash);
        Check.argument(entry != null, () -> String.format("Unknown resource: %s", hash));

        try {
            channel.position(entry.offset());
            var compressed = IOUtils.readBytes(channel, entry.size());
            return OozDecompressor.decompress(compressed, entry.uncompressedSize());
        } catch (IOException e) {
            System.out.println("Error reading resource: " + hash);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }
}
