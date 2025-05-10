package be.twofold.valen.export.cast;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.cast.mappers.*;
import be.twofold.valen.format.cast.*;

import java.io.*;
import java.nio.file.*;

public final class CastModelExporter extends CastExporter<Model> {
    @Override
    public String getID() {
        return "model.cast";
    }

    @Override
    public Class<Model> getSupportedType() {
        return Model.class;
    }

    @Override
    public void export(Model value, OutputStream out) throws IOException {
        // TODO: Eehm, Liskov fail for now
        throw new UnsupportedOperationException("GLTF can't export to a stream");
    }

    @Override
    public void export(Model value, Path path) throws IOException {
        Path imagePath = path.getParent().resolve("_images");
        if (!Files.exists(imagePath)) {
            Files.createDirectory(imagePath);
        }

        Cast cast = new Cast();
        CastNode.Root root = cast.createRoot();
        root.createMetadata()
            .setAuthor("JanDK")
            .setSoftware("Valen")
            .setUpAxis(CastNode.UpAxis.Z);

        new CastModelMapper(path, imagePath).map(value, root);

        try (var out = Files.newOutputStream(path)) {
            cast.write(out);
        }
    }
}
