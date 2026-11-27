package be.twofold.valen.export.cast;

import be.twofold.tinycast.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.cast.mappers.*;

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
    public void doExport(Model model, CastNodes.Root root, Path castPath, Path imagePath) throws IOException {
        writeModel(model, root, castPath, imagePath);
    }

    static void writeModel(Model model, CastNodes.Root root, Path castPath, Path imagePath) throws IOException {
        root.getMetadatas().getFirst()
            .setUpAxis(mapUpAxis(model.upAxis()));

        CastNodes.Model modelNode = new CastModelMapper(castPath, imagePath).map(model, root);
        model.hair().ifPresent(hair -> new CastHairMapper().map(modelNode, hair));
    }

    private static CastNodes.UpAxis mapUpAxis(Axis axis) {
        return switch (axis) {
            case X -> CastNodes.UpAxis.X;
            case Y -> CastNodes.UpAxis.Y;
            case Z -> CastNodes.UpAxis.Z;
        };
    }
}
