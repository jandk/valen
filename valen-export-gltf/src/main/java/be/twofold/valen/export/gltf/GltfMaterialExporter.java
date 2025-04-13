package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.format.gltf.*;

import java.io.*;
import java.util.*;

public final class GltfMaterialExporter extends GltfExporter<Material> {
    private final GltfModelExporter modelExporter = new GltfModelExporter();

    @Override
    public String getID() {
        return "material.gltf";
    }

    @Override
    public Class<Material> getSupportedType() {
        return Material.class;
    }

    @Override
    void doExport(Material material, GltfWriter writer) throws IOException {
        var mesh = MeshGenerator
            .createSphere(16, 16)
            .withMaterial(Optional.of(material));
        var model = new Model(List.of(mesh), Axis.Y);

        modelExporter.doExport(model, writer);
    }
}
