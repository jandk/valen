package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;

import java.io.*;
import java.nio.file.*;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void export(Model model, Path path) throws IOException {
        var writer = GltfWriter.createGlbWriter();
        var modelMapper = new GltfModelMultiMapper(writer);
        var rootNodeID = modelMapper.map(model);
        writer.addScene(List.of(rootNodeID));
        writer.write(path);
    }
}
