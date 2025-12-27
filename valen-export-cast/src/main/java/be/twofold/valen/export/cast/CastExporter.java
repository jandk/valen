package be.twofold.valen.export.cast;

import be.twofold.tinycast.*;
import be.twofold.valen.core.export.*;

import java.io.*;
import java.nio.file.*;

public abstract class CastExporter<T> implements Exporter<T> {
    @Override
    public String getName() {
        return "Cast";
    }

    @Override
    public String getExtension() {
        return "cast";
    }

    @Override
    public void export(T value, OutputStream out) {
        // TODO: Eehm, Liskov fail for now
        throw new UnsupportedOperationException("GLTF can't export to a stream");
    }

    @Override
    public void export(T value, Path path) throws IOException {
        var imagePath = path.getParent().resolve("_images");
        if (!Files.exists(imagePath)) {
            Files.createDirectory(imagePath);
        }

        var cast = Cast.create(0x5A4C524E454C4156L);
        var root = cast.createRoot();
        root.createMetadata()
            .setAuthor("JanDK")
            .setSoftware("Valen");

        doExport(value, root, path, imagePath);

        try (var out = Files.newOutputStream(path)) {
            cast.write(out);
        } catch (CastException e) {
            throw new IOException(e);
        }
    }

    public abstract void doExport(T value, CastNodes.Root root, Path castPath, Path imagePath) throws IOException;
}
