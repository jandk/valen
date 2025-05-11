package be.twofold.valen.export.cast;

import be.twofold.valen.core.export.MeshGenerator;
import be.twofold.valen.core.geometry.Model;
import be.twofold.valen.core.material.Material;
import be.twofold.valen.core.math.Axis;
import be.twofold.valen.format.cast.CastNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
    public void doExport(Material material, CastNode.Root root, Path castPath, Path imagePath) throws IOException {
        var mesh = MeshGenerator
                .createSphere(16, 16)
                .withMaterial(Optional.of(material));
        var model = new Model(List.of(mesh), Axis.Y);

        modelExporter.doExport(model, root, castPath, imagePath);
    }
}
