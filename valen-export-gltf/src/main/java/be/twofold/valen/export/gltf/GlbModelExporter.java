package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.node.*;

import java.io.*;
import java.util.*;

public final class GlbModelExporter implements Exporter<Model> {
    @Override
    public String getExtension() {
        return "glb";
    }

    @Override
    public Class<Model> getSupportedType() {
        return Model.class;
    }

    @Override
    public void export(Model model, OutputStream out) throws IOException {
        var context = new GltfContext();
        var rootNodes = new ArrayList<NodeID>();
        var materialMapper = new GltfMaterialMapper(context);
        var modelMapper = new GltfModelMapper(context);

        model.materials().stream().map(materialMapper::map).forEach(context::addMaterial);
        var modelRootNode = modelMapper.map(model);
        rootNodes.add(context.addNode(modelRootNode));

        context.addScene(rootNodes);

        var writer = new GlbWriter(context);
        writer.write(out);
    }
}
