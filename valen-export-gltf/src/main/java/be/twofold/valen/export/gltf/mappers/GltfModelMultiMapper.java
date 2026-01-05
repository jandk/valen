package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.mesh.*;
import be.twofold.valen.format.gltf.model.node.*;

import java.io.*;
import java.util.*;

public final class GltfModelMultiMapper extends GltfModelMapper {
    private final Map<String, NodeID> models = new HashMap<>();
    private final GltfContext context;

    public GltfModelMultiMapper(GltfContext context) {
        super(context);
        this.context = context;
    }

    public NodeID map(ModelReference model) throws IOException {
        var existingNodeID = models.get(model.name());
        if (existingNodeID != null) {
            return existingNodeID;
        }

        var nodeID = map(model.supplier().get());
        models.put(model.name(), nodeID);
        return nodeID;
    }

    public NodeID map(Model model) throws IOException {
        var meshIDs = mapModel(model).stream()
            .map(context::addMesh)
            .toList();

        var nodeIDs = model.skeleton().isPresent()
            ? mapAnimatedModel(meshIDs, model.skeleton().get())
            : mapStaticModel(meshIDs, model.upAxis());

        return context.addNode(
            ImmutableNode.builder()
                .name(model.name())
                .children(nodeIDs)
                .build());
    }

    private List<NodeID> mapStaticModel(List<MeshID> meshIDs, Axis axis) {
        return meshIDs.stream()
            .map(meshID -> context.addNode(ImmutableNode.builder()
                .rotation(GltfUtils.mapQuaternion(axis.rotateTo(Axis.Y)))
                .mesh(meshID)
                .build()))
            .toList();
    }

    private List<NodeID> mapAnimatedModel(List<MeshID> meshIDs, Skeleton skeleton) throws IOException {
        var skeletonMapper = new GltfSkeletonMapper(context);
        var skinID = skeletonMapper.map(skeleton);

        return meshIDs.stream()
            .map(meshID -> context.addNode(ImmutableNode.builder()
                .mesh(meshID)
                .skin(skinID)
                .build()))
            .toList();
    }

    private List<MeshSchema> mapModel(Model model) throws IOException {
        var meshSchemas = new ArrayList<MeshSchema>();
        for (var mesh : model.meshes()) {
            mapMesh(mesh).ifPresent(meshSchemas::add);
        }
        return meshSchemas;
    }

    private Optional<MeshSchema> mapMesh(Mesh mesh) throws IOException {
        var primitiveSchema = mapMeshPrimitive(mesh);
        if (primitiveSchema.isEmpty()) {
            return Optional.empty();
        }

        var morphTargetNames = mesh.blendShapes().stream()
            .map(BlendShape::name)
            .toList();

        var builder = ImmutableMesh.builder()
            .name(mesh.name())
            .addPrimitives(primitiveSchema.get());

        if (!morphTargetNames.isEmpty()) {
            builder.extras(Map.of("targetNames", morphTargetNames));
        }
        return Optional.of(builder.build());
    }
}
