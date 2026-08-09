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
        var meshes = mapMeshes(model);

        // Everything hangs under a single model root that carries the up-axis rotation: the joints
        // (so their world transforms pick it up) and the mesh nodes (so static meshes are oriented
        // too). The mesh nodes themselves carry no rotation.
        var children = new ArrayList<NodeID>();
        if (model.skeleton().isPresent()) {
            var skeletonMapper = new GltfSkeletonMapper(context);
            var mappedSkin = skeletonMapper.mapSkin(model.skeleton().get());
            children.add(mappedSkin.rootJoint());
            for (var mesh : meshes) {
                var node = ImmutableNode.builder().mesh(mesh.meshID());
                // A skin may only be attached to a mesh that actually carries JOINTS_n/WEIGHTS_n; an
                // unskinned mesh in a rigged model stays a plain child of the model root.
                if (mesh.skinned()) {
                    node.skin(mappedSkin.skin());
                }
                children.add(context.addNode(node.build()));
            }
        } else {
            for (var mesh : meshes) {
                children.add(context.addNode(ImmutableNode.builder()
                    .mesh(mesh.meshID())
                    .build()));
            }
        }

        return context.addNode(ImmutableNode.builder()
            .name(model.name())
            .rotation(GltfUtils.mapQuaternion(model.upAxis().rotateTo(Axis.Y)))
            .children(children)
            .build());
    }

    private List<MappedMesh> mapMeshes(Model model) throws IOException {
        var meshes = new ArrayList<MappedMesh>();
        for (var mesh : model.meshes()) {
            mapMesh(mesh).ifPresent(schema ->
                meshes.add(new MappedMesh(context.addMesh(schema), mesh.maxInfluence() != 0)));
        }
        return meshes;
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

    private record MappedMesh(
        MeshID meshID,
        boolean skinned
    ) {
    }
}
