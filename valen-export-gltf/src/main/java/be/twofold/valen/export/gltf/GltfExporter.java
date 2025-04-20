package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.format.gltf.*;

import java.io.*;
import java.nio.file.*;

public abstract class GltfExporter<T> implements Exporter<T> {
    private GltfExportMode mode;

    @Override
    public String getName() {
        return "glTF/GLB (GL Transmission Format)";
    }

    @Override
    public String getExtension() {
        return switch (mode) {
            case GLB -> "glb";
            case GLTF_SPLIT -> "gltf";
        };
    }

    @Override
    public void setProperty(String key, Object value) {
        if (key.equals("gltf.mode")) {
            mode = switch (value.toString()) {
                case "glb" -> GltfExportMode.GLB;
                case "gltf" -> GltfExportMode.GLTF_SPLIT;
                default -> throw new IllegalArgumentException("Unexpected value: " + value);
            };
        }
    }

    @Override
    public void export(T value, OutputStream out) throws IOException {
        // TODO: Eehm, Liskov fail for now
        throw new UnsupportedOperationException("GLTF can't export to a stream");
    }

    @Override
    @SuppressWarnings("resource")
    public void export(T value, Path path) throws IOException {
        var writer = switch (mode) {
            case GLB -> GltfWriter.createGlbWriter(path);
            case GLTF_SPLIT -> createSplitWriter(path);
        };

        try (writer) {
            doExport(value, writer);
        }
    }

    private static GltfWriter createSplitWriter(Path path) throws IOException {
        var outputDirectory = path.getParent();
        var fileName = path.getFileName().toString();
        var fileNameWithoutExtension = Filenames.removeExtension(fileName);

        var binPath = outputDirectory.resolve(fileNameWithoutExtension + ".bin");
        var imagePath = outputDirectory.resolve("_images");
        if (!Files.exists(imagePath)) {
            Files.createDirectories(imagePath);
        }

        return GltfWriter.createSplitWriter(path, binPath, imagePath);
    }

    abstract void doExport(T object, GltfWriter writer) throws IOException;
}
