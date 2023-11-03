package be.twofold.valen;

import be.twofold.valen.reader.packagemapspec.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public final class FileManager {
    private final Map<String, SeekableByteChannel> channels = new HashMap<>();
    private final Path base;
    private final PackageMapSpec spec;
    private final StreamDbManager streamDbManager;
    private final ResourcesManager resourcesManager;

    public FileManager(Path base) {
        this.base = Objects.requireNonNull(base);
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.streamDbManager = new StreamDbManager(this);
        this.resourcesManager = new ResourcesManager(this);
    }

    public PackageMapSpec getSpec() {
        return spec;
    }

    public StreamDbManager getStreamDbManager() {
        return streamDbManager;
    }

    public ResourcesManager getResourcesManager() {
        return resourcesManager;
    }

    public void select(String map) {
        List<String> resources = channels.keySet().stream()
            .filter(e -> e.endsWith(".resources"))
            .toList();

        resources.forEach(this::close);

        resourcesManager.select(map);
    }

    public SeekableByteChannel open(String path) {
        if (channels.containsKey(path)) {
            return channels.get(path);
        }

        try {
            SeekableByteChannel channel = Files.newByteChannel(base.resolve(path), StandardOpenOption.READ);
            channels.put(path, channel);
            return channel;
        } catch (IOException e) {
            System.err.println("Failed to open file: " + path);
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("resource")
    private void close(String path) {
        SeekableByteChannel channel = channels.get(path);
        if (channel == null) {
            return;
        }

        try {
            channel.close();
            channels.remove(path);
        } catch (IOException e) {
            System.err.println("Failed to close file: " + path);
            throw new UncheckedIOException(e);
        }
    }
}
