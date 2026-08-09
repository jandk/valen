package be.twofold.valen.game.doom.vmtr;

import be.twofold.valen.game.doom.mega2.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * The contents of the {@code virtualtextures} folder: the {@code .vmtr} manifests, and the
 * {@code .mega2} files holding the pages they point into.
 */
public record VmtrIndex(
    Map<Path, BinarySource> sources,
    List<VmtrEntry> entries,
    VmtrAtlas atlas
) {
    public static final int GRID_SIZE = 4;

    private static final String VMTR_EXTENSION = ".vmtr";

    public VmtrIndex {
        sources = Map.copyOf(sources);
        entries = List.copyOf(entries);
    }

    public static VmtrIndex build(Path virtualTextures) throws IOException {
        if (!Files.isDirectory(virtualTextures)) {
            return new VmtrIndex(Map.of(), List.of(), new VmtrAtlas(List.of()));
        }

        var entries = new ArrayList<VmtrEntry>();
        for (var path : findManifests(virtualTextures)) {
            entries.addAll(Vmtr.read(path).entries());
        }

        var sources = new LinkedHashMap<Path, BinarySource>();
        var pageFiles = new ArrayList<Mega2File>();
        for (int i = 1; i <= GRID_SIZE * GRID_SIZE; i++) {
            var path = virtualTextures.resolve("_vmtr_sq" + i + ".mega2");
            var source = BinarySource.open(path);
            sources.put(path, source);
            pageFiles.add(Mega2File.open(source));
        }

        return new VmtrIndex(sources, entries, new VmtrAtlas(pageFiles));
    }

    private static List<Path> findManifests(Path virtualTextures) throws IOException {
        try (var paths = Files.list(virtualTextures)) {
            return paths
                .filter(path -> path.getFileName().toString().endsWith(VMTR_EXTENSION))
                .sorted()
                .toList();
        }
    }

    @Override
    public String toString() {
        return "VmtrIndex(" +
            "sources=[" + sources.size() + " items], " +
            "entries=[" + entries.size() + " items]" +
            ")";
    }
}
