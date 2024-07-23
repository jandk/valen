package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.export.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.*;

import java.io.*;
import java.util.*;

public final class GlbModelExporter implements Exporter<Model> {
    // TODO: Make this configurable
    private static final Quaternion ROTATION = Quaternion.fromAxisAngle(Vector3.UnitX, -MathF.HALF_PI);

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

        var meshId = context.addMesh(
            modelMapper.map(model));

        var rootNodeId = model.skeleton() != null
            ? mapAnimatedModel(context, meshId, model.skeleton())
            : mapStaticModel(context, meshId);

        context.addScene(List.of(rootNodeId));

        var writer = new GlbWriter(context);
        writer.write(out);
    }

    private static NodeId mapAnimatedModel(GltfContext context, MeshId meshId, Skeleton skeleton) {
        var skeletonMapper = new GltfSkeletonMapper(context, ROTATION);

        var skinId = context.addSkin(
            skeletonMapper.map(skeleton));

        var meshNodeId = context.addNode(
            NodeSchema.builder()
                .mesh(meshId)
                .skin(skinId)
                .build());

        return context.addNode(
            NodeSchema.builder()
                .addChildren(meshNodeId)
                .build());
    }

    private static NodeId mapStaticModel(GltfContext context, MeshId meshId) {
        var meshNodeId = context.addNode(
            NodeSchema.builder()
                .mesh(meshId)
                .build());

        return context.addNode(
            NodeSchema.builder()
                .rotation(GltfUtils.mapQuaternion(ROTATION))
                .addChildren(meshNodeId)
                .build());
    }
}
