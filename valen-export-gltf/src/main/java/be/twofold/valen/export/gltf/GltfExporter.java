package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.format.gltf.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;

public abstract class GltfExporter<T> implements Exporter<T> {
    private final GltfExportMode mode;

    protected GltfExporter(GltfExportMode mode) {
        this.mode = Check.nonNull(mode, "mode");
    }

    @Override
    public final String getName() {
        return mode.displayName();
    }

    @Override
    public final String getExtension() {
        return mode.extension();
    }

    @Override
    public void export(T value, OutputStream out) throws IOException {
        // TODO: Eehm, Liskov fail for now
        throw new UnsupportedOperationException("GLTF can't export to a stream");
    }

    @Override
    public void export(T value, Path path) throws IOException {
        var writer = switch (mode) {
            case GLB -> GltfWriter.createGlbWriter(path, Valen.software());
            case GLTF_SPLIT -> createSplitWriter(path);
        };

        try (writer) {
            doExport(value, writer);
        }
    }

    private static GltfWriter createSplitWriter(Path path) throws IOException {
        var binPath = PathUtils.replaceExtension(path, "bin");
        var imagePath = path.getParent().resolve("_images");
        if (!Files.exists(imagePath)) {
            Files.createDirectories(imagePath);
        }

        return GltfWriter.createSplitWriter(path, binPath, imagePath, Valen.software());
    }

    abstract void doExport(T object, GltfWriter writer) throws IOException;
}
