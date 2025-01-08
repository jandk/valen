package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GlbModelExporter implements Exporter<Model> {
    private final GltfContext context = new GltfContext();
    private final GltfModelMultiMapper modelMapper = new GltfModelMultiMapper(context, null);

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
        throw new UnsupportedOperationException();
    }

    @Override
    public void export(Model model, Path path) throws IOException {
        var rootNodeID = modelMapper.map(model);

        context.addScene(List.of(rootNodeID));

        var writer = new GltfWriter(context);
        writer.write(path);
    }
}
