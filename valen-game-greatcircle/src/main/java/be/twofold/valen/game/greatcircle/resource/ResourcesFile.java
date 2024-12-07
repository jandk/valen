package be.twofold.valen.game.greatcircle.resource;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;

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
        var type = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + entry.resourceTypeString()]);
        var name = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + entry.nameString()]);

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromName(type);
        var resourceVariation = ResourceVariation.fromValue(entry.variation());
        var resourceKey = new ResourceKey(resourceName, resourceType, resourceVariation);

        if(entry.defaultHash() == 8119285997658497211L){
            System.out.println();
        }

        return new Resource(
            resourceKey,
            entry.dataOffset(),
            entry.dataSize(),
            entry.uncompressedSize(),
            entry.compMode(),
            entry.defaultHash(),
            entry.dataCheckSum(),
            entry.version()
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
