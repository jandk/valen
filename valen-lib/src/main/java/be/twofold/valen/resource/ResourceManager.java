package be.twofold.valen.resource;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.packagemapspec.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ResourceManager implements AutoCloseable {
    private final Path base;
    private final PackageMapSpec spec;
    private List<ResourcesFile> files;
    private Map<String, ResourcesFile> index;

    public ResourceManager(Path base, PackageMapSpec spec) {
        this.base = Check.notNull(base, "base must not be null");
        this.spec = Check.notNull(spec, "spec must not be null");
    }

    public Resource getEntry(String name) {
        ResourcesFile file = index.get(name);
        Check.argument(file != null, () -> String.format("Unknown resource: %s", name));

        return file.getEntry(name);
    }

    public byte[] read(String name) {
        var file = index.get(name);
        Check.argument(file != null, () -> String.format("Unknown resource: %s", name));

        return file.read(name);
    }

    public void select(String map) throws IOException {
        var mapFiles = spec.mapFiles().get(map);
        Check.argument(mapFiles != null, () -> "Unknown map: " + map);

        close();

        var paths = mapFiles.stream()
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<ResourcesFile>();
        var index = new HashMap<String, ResourcesFile>();
        for (var path : paths) {
            var file = new ResourcesFile(path);
            files.add(file);
            for (var entry : file.getEntries()) {
                index.put(entry.name().name(), file);
            }
        }
        this.files = List.copyOf(files);
        this.index = Map.copyOf(index);
    }

    @Override
    public void close() throws IOException {
        if (files != null) {
            for (var file : files) {
                file.close();
            }
            files = null;
            index = null;
        }
    }
}
