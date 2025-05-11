package be.twofold.valen.export.cast;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.export.cast.mappers.*;
import be.twofold.valen.format.cast.*;

import java.io.*;
import java.nio.file.*;

public final class CastModelExporter extends CastExporter<Model> {
    private final CastHairMapper hairMapper = new CastHairMapper();

    @Override
    public String getID() {
        return "model.cast";
    }

    @Override
    public Class<Model> getSupportedType() {
        return Model.class;
    }

    @Override
    public void doExport(Model model, CastNode.Root root, Path castPath, Path imagePath) throws IOException {
        root.getMetadatas().getFirst()
            .setUpAxis(mapUpAxis(model.upAxis()));

        CastNode.Model modelNode = new CastModelMapper(castPath, imagePath).map(model, root);
        model.hair().ifPresent(hair -> hairMapper.map(modelNode, hair));
    }

    private CastNode.UpAxis mapUpAxis(Axis axis) {
        return switch (axis) {
            case X -> CastNode.UpAxis.X;
            case Y -> CastNode.UpAxis.Y;
            case Z -> CastNode.UpAxis.Z;
        };
    }
}
