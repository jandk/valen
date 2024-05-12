package be.twofold.valen.resource;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.resource.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements AutoCloseable {
    private final Map<ResourceKey, Resource> index;
    private DataSource source;

    public ResourcesFile(Path path) throws IOException {
        System.out.println("Loading resources: " + path);

        this.source = new ChannelDataSource(Files.newByteChannel(path, StandardOpenOption.READ));
        List<Resource> resources = new ResourceMapper().map(Resources.read(source));

        this.index = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                Resource::key,
                Function.identity()
            ));
    }

    public Collection<Resource> getResources() {
        return index.values();
    }

    public Resource getEntry(ResourceKey key) {
        return index.get(key);
    }

    public byte[] read(ResourceKey key) {
        var entry = index.get(key);
        Check.argument(entry != null, () -> String.format("Unknown resource: %s", key));

        try {
            source.seek(entry.offset());
            var compressed = source.readBytes(entry.size());
            return OodleDecompressor.decompress(compressed, entry.uncompressedSize());
        } catch (IOException e) {
            System.out.println("Error reading resource: " + key);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (source != null) {
            // TODO: Implement autocloseable
            // source.close();
            source = null;
        }
    }
}
