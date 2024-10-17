package be.twofold.valen.export.gltf;

import be.twofold.valen.core.scene.*;
import be.twofold.valen.export.*;
import be.twofold.valen.export.gltf.mappers.GltfUtils;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.node.*;

import java.io.*;
import java.util.*;

public final class GlbSceneExporter implements Exporter<Scene> {
    private final GltfContext context = new GltfContext();
    private final GltfModelMapper modelMapper = new GltfModelMapper(context);

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
            var nodeID = modelMapper.map(instance.modelReference());

            var instanceNode = context.addNode(NodeSchema.builder()
                .name(Optional.ofNullable(instance.name()))
                .translation(GltfUtils.mapVector3(instance.translation()))
                .rotation(GltfUtils.mapQuaternion(instance.rotation()))
                .scale(GltfUtils.mapVector3(instance.scale()))
                .children(List.of(nodeID))
                .build());
            instanceNodes.add(instanceNode);
        }

        context.addScene(instanceNodes);

        var writer = new GlbWriter(context);
        writer.write(out);
    }
}
