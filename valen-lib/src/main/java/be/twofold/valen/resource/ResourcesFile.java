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
    private final Map<String, Resource> entries;
    private SeekableByteChannel channel;

    public ResourcesFile(Path path) throws IOException {
        System.out.println("Loading resources: " + path);

        this.channel = Files.newByteChannel(path, StandardOpenOption.READ);
        List<Resource> resources = new ResourceMapper().map(Resources.read(channel));
        this.entries = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                resource -> resource.name().toString(),
                Function.identity(),
                (first, second) -> first
            ));
    }

    public Collection<Resource> getEntries() {
        return entries.values();
    }

    public Resource getEntry(String name) {
        return entries.get(name);
    }

    public byte[] read(String name) {
        var entry = entries.get(name);
        Check.argument(entry != null, () -> String.format("Unknown resource: %s", name));

        try {
            channel.position(entry.offset());
            var compressed = IOUtils.readBytes(channel, entry.size());
            return OodleDecompressor.decompress(compressed, entry.uncompressedSize());
        } catch (IOException e) {
            System.out.println("Error reading resource: " + name);
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
