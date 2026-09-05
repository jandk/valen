package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.format.gltf.*;

import java.io.*;
import java.util.*;

public abstract class GltfModelExporter extends GltfExporter<Model> {
    GltfModelExporter(GltfExportMode mode) {
        super(mode);
    }

    @Override
    public Class<Model> getSupportedType() {
        return Model.class;
    }

    @Override
    void doExport(Model model, GltfWriter writer) throws IOException {
        writeModel(model, writer);
    }

    static void writeModel(Model model, GltfWriter writer) throws IOException {
        var modelMapper = new GltfModelMultiMapper(writer);
        var rootNodeID = modelMapper.map(model);
        writer.addScene(List.of(rootNodeID));
    }

    public static final class Binary extends GltfModelExporter {
        public Binary() {
            super(GltfExportMode.GLB);
        }

        @Override
        public String getID() {
            return "model.glb";
        }
    }

    public static final class Split extends GltfModelExporter {
        public Split() {
            super(GltfExportMode.GLTF_SPLIT);
        }

        @Override
        public String getID() {
            return "model.gltf";
        }
    }
}
