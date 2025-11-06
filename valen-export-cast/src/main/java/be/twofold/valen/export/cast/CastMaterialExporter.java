package be.twofold.valen.export.cast;

import be.twofold.tinycast.*;
import be.twofold.valen.core.export.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class CastMaterialExporter extends CastExporter<Material> {
    private final CastModelExporter modelExporter = new CastModelExporter();

    @Override
    public String getID() {
        return "material.cast";
    }

    @Override
    public Class<Material> getSupportedType() {
        return Material.class;
    }

    @Override
    public void doExport(Material material, CastNodes.Root root, Path castPath, Path imagePath) throws IOException {
        var mesh = MeshGenerator
                .createSphere(16, 16)
                .withMaterial(Optional.of(material));
        var model = new Model(List.of(mesh), Axis.Y);

        modelExporter.doExport(model, root, castPath, imagePath);
    }
}
