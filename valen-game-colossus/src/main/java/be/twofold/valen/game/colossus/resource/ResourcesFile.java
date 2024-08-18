package be.twofold.valen.game.colossus.resource;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.colossus.reader.resources.*;

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

        var resources = mapResources(Resources.read(source));

//        try {
//            var csv = CsvUtils.toCsv(List.of(), resources, Resource.class);
//            Files.write(Paths.get("resources.csv"), csv.getBytes());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        this.index = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                Resource::key,
                Function.identity()
            ));
    }

    public Collection<Resource> getResources() {
        return index.values();
    }

    public Optional<Resource> get(ResourceKey key) {
        return Optional.ofNullable(index.get(key));
    }

    public byte[] read(Resource resource) throws IOException {
        source.seek(resource.offset());
        return source.readBytes(resource.compressedSize());
    }

    private List<Resource> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry))
            .toList();
    }

    private Resource mapResourceEntry(Resources resources, ResourceEntry entry) {
        var name = resources.strings().get(resources.stringIndex()[entry.strings() + 1]);
        var type = resources.strings().get(resources.stringIndex()[entry.strings()]);

        var key = new ResourceKey(
            new ResourceName(name),
            ResourceType.valueOf(type),
            ResourceVariation.valueOf(entry.options().variation())
        );

        var compression = switch (entry.options().compMode()) {
            case 0 -> CompressionType.None;
            case 2 -> CompressionType.Kraken;
            case 4 -> CompressionType.KrakenChunked;
            default -> throw new UnsupportedOperationException("Compression " + entry.options().compMode());
        };

        return new Resource(
            new ResourceName(name),
            ResourceType.valueOf(type),
            ResourceVariation.valueOf(entry.options().variation()),
            Math.toIntExact(entry.dataOffset()),
            Math.toIntExact(entry.dataSize()),
            Math.toIntExact(entry.options().uncompressedSize()),
            compression,
            entry.options().defaultHash()
        );
    }

    @Override
    public void close() throws IOException {
        if (source != null) {
            source.close();
            source = null;
        }
    }
}
