package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;

import java.io.*;
import java.util.*;

public final class GlbModelExporter implements Exporter<Model> {
    private final GltfContext context = new GltfContext();
    private final GltfModelMultiMapper modelMapper = new GltfModelMultiMapper(context);

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
        var rootNodeID = modelMapper.map(model);

        context.addScene(List.of(rootNodeID));

        var writer = new GlbWriter(context);
        writer.write(out);
    }
}
