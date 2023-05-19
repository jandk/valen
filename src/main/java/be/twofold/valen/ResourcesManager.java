package be.twofold.valen;

import be.twofold.valen.model.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.reader.resource.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public final class ResourcesManager {

    private final Path basePath;
    private final PackageMapSpec packageMapSpec;
    private Map<String, SeekableByteChannel> pathToChannel;
    private Map<FileEntry, String> entryToPath;

    private ResourcesManager(Path basePath, PackageMapSpec packageMapSpec) {
        this.basePath = basePath;
        this.packageMapSpec = packageMapSpec;
    }

    public static ResourcesManager load(Path basePath, PackageMapSpec packageMapSpec) {
        return new ResourcesManager(basePath, packageMapSpec);
    }

    public Collection<FileEntry> getEntries() {
        return entryToPath.keySet();
    }

    public byte[] read(FileEntry entry) throws IOException {
        String path = entryToPath.get(entry);
        SeekableByteChannel channel = pathToChannel.get(path);

        channel.position(entry.offset());
        byte[] compressed = IOUtils.readBytes(channel, entry.size());
        return OodleDecompressor.decompress(compressed, entry.sizeUncompressed());
    }

    public void select(String map) throws IOException {
        close();

        List<String> paths = packageMapSpec.mapFiles().get(map).stream()
            .filter(p -> p.endsWith(".resources"))
            .toList();

        Map<String, SeekableByteChannel> pathToChannel = new HashMap<>();
        Map<FileEntry, String> entryToPath = new HashMap<>();
        for (String path : paths) {
            System.out.println("Loading resources: " + path);
            Path fullPath = basePath.resolve(path);
            SeekableByteChannel channel = Files.newByteChannel(fullPath);
            pathToChannel.put(path, channel);

            Resources resources = new ResourcesReader(channel).read(false);
            List<FileEntry> entries = FileEntryMapper.mapEntries(resources);
            for (FileEntry entry : entries) {
                entryToPath.putIfAbsent(entry, path);
            }
        }
        this.pathToChannel = Map.copyOf(pathToChannel);
        this.entryToPath = Map.copyOf(entryToPath);
    }

    private void close() {
        if (pathToChannel == null) {
            return;
        }
        for (SeekableByteChannel channel : pathToChannel.values()) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to close channel", e);
            }
        }
        pathToChannel = null;
        entryToPath = null;
    }
}
