package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.format.gltf.*;

import java.io.*;
import java.nio.file.*;

public abstract class AbstractGltfExporter<T> implements Exporter<T> {
    @Override
    public String getName() {
        return "glTF/GLB (GL Transmission Format)";
    }

    @Override
    public String getExtension() {
        return "glb";
    }

    @Override
    public void export(T value, OutputStream out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void export(T value, Path path) throws IOException {
        var outputDirectory = path.getParent();
        var fileName = path.getFileName().toString();
        var fileNameWithoutExtension = Filenames.removeExtension(fileName);

        var binPath = outputDirectory.resolve(fileNameWithoutExtension + ".bin");
        var imagePath = outputDirectory.resolve("_images");

        // try (var writer = GltfWriter.createSplitWriter(path, binPath, imagePath)) {
        try (var writer = GltfWriter.createGlbWriter(path)) {
            doExport(value, writer);
        }
    }

    abstract void doExport(T object, GltfWriter writer) throws IOException;
}
