package be.twofold.valen.manager;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.reader.streamdb.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public final class FileManager {
    private final Map<String, SeekableByteChannel> channels = new HashMap<>();
    private final Path base;
    private final PackageMapSpec spec;
    private final StreamManager streamManager;
    private final ResourceManager resourceManager;

    public FileManager(Path base) throws IOException {
        this.base = Check.notNull(base);
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.streamManager = new StreamManager(this);
        this.resourceManager = new ResourceManager(this);
    }

    public PackageMapSpec getSpec() {
        return spec;
    }

    public Resource getResourceEntry(String name) {
        return resourceManager.getEntry(name);
    }

    public List<Resource> getResourceEntries() {
        return List.copyOf(resourceManager.getEntries());
    }

    public Collection<StreamDbEntry> getStreamEntries() {
        return streamManager.getEntries();
    }

    public void select(String map) throws IOException {
        List<String> resources = channels.keySet().stream()
            .filter(e -> e.endsWith(".resources"))
            .toList();

        resources.forEach(this::close);

        resourceManager.select(map);
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

    public BetterBuffer readResource(Resource entry) {
        try {
            return BetterBuffer.wrap(resourceManager.read(entry));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public BetterBuffer readStream(long identity, int size) {
        return BetterBuffer.wrap(streamManager.load(identity, size));
    }

    public boolean streamExists(long identity) {
        return streamManager.exists(identity);
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
