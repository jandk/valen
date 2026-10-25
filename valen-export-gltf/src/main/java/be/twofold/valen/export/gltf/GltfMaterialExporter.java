package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.format.gltf.*;

import java.io.*;
import java.util.*;

public abstract class GltfMaterialExporter extends GltfExporter<Material> {
    GltfMaterialExporter(GltfExportMode mode) {
        super(mode);
    }

    @Override
    public Class<Material> getSupportedType() {
        return Material.class;
    }

    @Override
    void doExport(Material material, GltfWriter writer) throws IOException {
        var mesh = MeshGenerator
            .createSphere(16, 16)
            .material(material)
            .build();
        var model = new Model(List.of(mesh), Axis.Y);

        GltfModelExporter.writeModel(model, writer);
    }

    public static final class Binary extends GltfMaterialExporter {
        public Binary() {
            super(GltfExportMode.GLB);
        }

        @Override
        public String getID() {
            return "material.glb";
        }
    }

    public static final class Split extends GltfMaterialExporter {
        public Split() {
            super(GltfExportMode.GLTF_SPLIT);
        }

        @Override
        public String getID() {
            return "material.gltf";
        }
    }
}
