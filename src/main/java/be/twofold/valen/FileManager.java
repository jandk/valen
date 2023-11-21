package be.twofold.valen;

import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.reader.resource.*;

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

    public ResourcesEntry getResourceEntry(String name) {
        return resourcesManager.getEntry(name);
    }

    public List<ResourcesEntry> getResourceEntries() {
        return List.copyOf(resourcesManager.getEntries());
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

    public BetterBuffer readResource(ResourcesEntry entry) {
        try {
            return BetterBuffer.wrap(resourcesManager.read(entry));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public BetterBuffer readStream(long identity, int size) {
        return BetterBuffer.wrap(streamDbManager.load(identity, size));
    }

    public boolean streamExists(long identity) {
        return streamDbManager.exists(identity);
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
