package be.twofold.valen.resource;

import be.twofold.valen.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.resource.*;

import java.io.*;
import java.util.*;

public final class ResourceManager {

    private final Map<Resource, String> resourceToPath = new HashMap<>();
    private final Map<String, Resource> nameToResource = new HashMap<>();
    private final FileManager fileManager;

    public ResourceManager(FileManager fileManager) {
        this.fileManager = Check.notNull(fileManager);
    }

    public Collection<Resource> getEntries() {
        return resourceToPath.keySet();
    }

    public Resource getEntry(String name) {
        return nameToResource.get(name);
    }

    public byte[] read(Resource entry) throws IOException {
        var channel = fileManager.open(resourceToPath.get(entry));
        channel.position(entry.offset());

        var compressed = IOUtils.readBytes(channel, entry.size());
        return OodleDecompressor.decompress(compressed, entry.uncompressedSize());
    }

    public void select(String map) throws IOException {
        var paths = fileManager.getSpec().mapFiles().get(map).stream()
            .filter(p -> p.endsWith(".resources"))
            .toList();

        resourceToPath.clear();
        nameToResource.clear();
        for (var path : paths) {
            System.out.println("Loading resources: " + path);

            var channel = fileManager.open(path);
            var resources = ResourceMapper.map(Resources.read(channel));
            for (var entry : resources) {
                resourceToPath.putIfAbsent(entry, path);
                nameToResource.putIfAbsent(entry.name().toString(), entry);
            }
        }
    }

}
