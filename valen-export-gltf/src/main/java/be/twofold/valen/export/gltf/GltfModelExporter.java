package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.format.gltf.*;

import java.io.*;
import java.util.*;

public final class GltfModelExporter extends GltfExporter<Model> {
    @Override
    public String getID() {
        return "model.gltf";
    }

    @Override
    public Class<Model> getSupportedType() {
        return Model.class;
    }

    @Override
    void doExport(Model model, GltfWriter writer) throws IOException {
        var modelMapper = new GltfModelMultiMapper(writer);
        var rootNodeID = modelMapper.map(model);
        writer.addScene(List.of(rootNodeID));
    }
}
