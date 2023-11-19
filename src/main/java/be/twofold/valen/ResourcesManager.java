package be.twofold.valen;

import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.resource.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public final class ResourcesManager {

    private final Map<ResourcesEntry, String> entryToPath = new HashMap<>();
    private final Map<String, ResourcesEntry> nameToEntry = new HashMap<>();
    private final FileManager fileManager;

    ResourcesManager(FileManager fileManager) {
        this.fileManager = Objects.requireNonNull(fileManager);
    }

    public Collection<ResourcesEntry> getEntries() {
        return entryToPath.keySet();
    }

    public ResourcesEntry getEntry(String name) {
        return nameToEntry.get(name);
    }

    public byte[] read(ResourcesEntry entry) throws IOException {
        String path = entryToPath.get(entry);

        SeekableByteChannel channel = fileManager.open(path);
        channel.position(entry.dataOffset());

        byte[] compressed = IOUtils.readBytes(channel, entry.dataSize());
        return OodleDecompressor.decompress(compressed, entry.uncompressedSize());
    }

    public void select(String map) {
        List<String> paths = fileManager.getSpec().mapFiles().get(map).stream()
            .filter(p -> p.endsWith(".resources"))
            .toList();

        entryToPath.clear();
        for (String path : paths) {
            System.out.println("Loading resources: " + path);

            SeekableByteChannel channel = fileManager.open(path);
            Resources resources = ResourcesReader.read(channel);
            for (ResourcesEntry entry : resources.entries()) {
                entryToPath.putIfAbsent(entry, path);
                nameToEntry.putIfAbsent(entry.name().toString(), entry);
            }
        }
    }

}
