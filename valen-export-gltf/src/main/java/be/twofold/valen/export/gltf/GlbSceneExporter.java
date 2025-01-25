package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.scene.*;
import be.twofold.valen.export.gltf.mappers.GltfUtils;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GlbSceneExporter implements Exporter<Scene> {
    private final GltfWriter writer = GltfWriter.createGlbWriter();
    private final GltfModelSingleMapper modelMapper = new GltfModelSingleMapper(writer, null);

    @Override
    public String getExtension() {
        return "glb";
    }

    @Override
    public Class<Scene> getSupportedType() {
        return Scene.class;
    }

    @Override
    public void export(Scene scene, Path path) throws IOException {
        var instanceNodes = new ArrayList<NodeID>();
        for (var instance : scene.instances()) {
            modelMapper.map(instance.modelReference())
                .ifPresent(meshID -> instanceNodes.add(mapInstance(instance, meshID)));
        }

        writer.addScene(instanceNodes);
        writer.write(path);
    }

    @Override
    public void export(Scene scene, OutputStream out) throws IOException {
        throw new UnsupportedOperationException("Cannot export a scene to a stream for now");
    }

    private NodeID mapInstance(Instance instance, MeshID meshID) {
        return writer.addNode(ImmutableNode.builder()
            .name(Optional.ofNullable(instance.name()))
            .translation(GltfUtils.mapVector3(instance.translation()))
            .rotation(GltfUtils.mapQuaternion(instance.rotation()))
            .scale(GltfUtils.mapVector3(instance.scale()))
            .mesh(meshID)
            .build());
    }
}
