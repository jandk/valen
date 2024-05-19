package be.twofold.valen.resource;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.resource.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements AutoCloseable {
    private final Path path;
    private final Map<ResourceKey, Resource> index;
    private SeekableByteChannel channel;
    private DataSource source;

    public ResourcesFile(Path path) throws IOException {
        System.out.println("Loading resources: " + path);

        this.path = path;
        this.channel = Files.newByteChannel(path, StandardOpenOption.READ);
        this.source = new ChannelDataSource(this.channel);
        var resources = mapResources(Resources.read(source));

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

    public byte[] read(ResourceKey key) throws IOException {
        var entry = index.get(key);
        Check.argument(entry != null, () -> String.format("Unknown resource: %s", key));
        return IOUtils.read(channel, entry.offset(), entry.compressedSize());
    }

    private List<Resource> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry))
            .toList();
    }

    private Resource mapResourceEntry(Resources resources, ResourcesEntry entry) {
        var type = resources.pathStrings().get(resources.pathStringIndex()[entry.strings()]);
        var name = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + 1]);

        return new Resource(
            new ResourceName(name),
            ResourceType.fromName(type),
            ResourceVariation.fromValue(entry.variation()),
            entry.dataOffset(),
            entry.dataSize(),
            entry.uncompressedSize(),
            mapCompressionType(entry.compMode()),
            entry.defaultHash()
        );
    }

    private CompressionType mapCompressionType(ResourceCompressionMode mode) {
        return switch (mode) {
            case RES_COMP_MODE_NONE -> CompressionType.None;
            case RES_COMP_MODE_KRAKEN -> CompressionType.Kraken;
            case RES_COMP_MODE_KRAKEN_CHUNKED -> CompressionType.KrakenChunked;
            default -> throw new UnsupportedOperationException("Unsupported compression mode: " + mode);
        };
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
