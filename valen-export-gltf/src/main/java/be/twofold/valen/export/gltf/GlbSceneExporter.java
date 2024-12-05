package be.twofold.valen.export.gltf;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.scene.*;
import be.twofold.valen.export.gltf.mappers.GltfUtils;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;

import java.io.*;
import java.util.*;

public final class GlbSceneExporter implements Exporter<Scene> {
    private final GltfContext context = new GltfContext();
    private final GltfModelSingleMapper modelMapper = new GltfModelSingleMapper(context);

    @Override
    public String getExtension() {
        return "glb";
    }

    @Override
    public Class<Scene> getSupportedType() {
        return Scene.class;
    }

    @Override
    public void export(Scene scene, OutputStream out) throws IOException {
        var instanceNodes = new ArrayList<NodeID>();
        for (var instance : scene.instances()) {
            modelMapper.map(instance.modelReference())
                .ifPresent(meshID -> instanceNodes.add(mapInstance(instance, meshID)));
        }

        context.addScene(instanceNodes);

        var writer = new GlbWriter(context);
        writer.write(out);
    }

    private NodeID mapInstance(Instance instance, MeshID meshID) {
        return context.addNode(NodeSchema.builder()
            .name(Optional.ofNullable(instance.name()))
            .translation(GltfUtils.mapVector3(instance.translation()))
            .rotation(GltfUtils.mapQuaternion(instance.rotation()))
            .scale(GltfUtils.mapVector3(instance.scale()))
            .mesh(meshID)
            .build());
    }
}
