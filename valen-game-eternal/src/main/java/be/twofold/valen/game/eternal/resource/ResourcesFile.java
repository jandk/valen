package be.twofold.valen.game.eternal.resource;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.resource.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements Closeable {
    private final Map<ResourceKey, Resource> index;
    private DataSource source;

    public ResourcesFile(Path path) throws IOException {
        System.out.println("Loading resources: " + path);
        this.source = DataSource.fromPath(path);

        var resources = mapResources(Resources.read(source));
        this.index = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                Resource::key,
                Function.identity()
            ));
    }


    public Optional<Resource> get(ResourceKey key) {
        return Optional.ofNullable(index.get(key));
    }

    public byte[] read(Resource resource) throws IOException {
        source.seek(resource.offset());
        return source.readBytes(resource.compressedSize());
    }

    public Collection<Resource> getResources() {
        return index.values();
    }


    private List<Resource> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry))
            .toList();
    }

    private Resource mapResourceEntry(Resources resources, ResourcesEntry entry) {
        var type = resources.pathStrings().get(resources.pathStringIndex()[entry.strings()]);
        var name = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + 1]);

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromName(type);
        var resourceVariation = ResourceVariation.fromValue(entry.variation());
        var resourceKey = new ResourceKey(resourceName, resourceType, resourceVariation);
        return new Resource(
            resourceKey,
            entry.dataOffset(),
            entry.dataSize(),
            entry.uncompressedSize(),
            entry.compMode(),
            entry.defaultHash(),
            entry.dataCheckSum()
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
