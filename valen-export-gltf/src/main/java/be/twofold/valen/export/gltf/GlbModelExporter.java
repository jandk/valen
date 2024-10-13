package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.export.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;

import java.io.*;
import java.util.*;

public final class GlbModelExporter implements Exporter<Model> {
    // TODO: Make this configurable
    private static final Quaternion ROTATION = Quaternion.fromAxisAngle(Vector3.X, -MathF.HALF_PI);

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
        var modelMapper = new GltfModelMapper(context);

        var meshIDs = modelMapper.map(model).stream()
            .map(context::addMesh)
            .toList();

        var rootNodeID = model.skeleton() != null
            ? mapAnimatedModel(context, meshIDs, model.skeleton())
            : mapStaticModel(context, meshIDs);

        context.addScene(List.of(rootNodeID));

        var writer = new GlbWriter(context);
        writer.write(out);
    }

    private static NodeID mapAnimatedModel(GltfContext context, List<MeshID> meshIDs, Skeleton skeleton) {
        var skeletonMapper = new GltfSkeletonMapper(context, ROTATION);

        var skinID = context.addSkin(skeletonMapper.map(skeleton));

        var meshNodeIDs = meshIDs.stream()
            .map(meshID -> context.addNode(NodeSchema.builder().mesh(meshID).skin(skinID).build()))
            .toList();

        return context.addNode(
            NodeSchema.builder()
                .addAllChildren(meshNodeIDs)
                .build());
    }

    private static NodeID mapStaticModel(GltfContext context, List<MeshID> meshIDs) {
        var meshNodeIDs = meshIDs.stream()
            .map(meshID -> context.addNode(NodeSchema.builder().mesh(meshID).build()))
            .toList();

        return context.addNode(
            NodeSchema.builder()
                // .rotation(GltfUtils.mapQuaternion(ROTATION))
                .addAllChildren(meshNodeIDs)
                .build());
    }
}
