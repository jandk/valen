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
    private final GltfSkeletonMapper skeletonMapper;

    public GltfModelMultiMapper(GltfContext context) {
        super(context);
        this.context = context;
        this.skeletonMapper = new GltfSkeletonMapper(context);
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

        var nodeIDs = model.skeleton() != null
            ? mapAnimatedModel(meshIDs, model.skeleton())
            : mapStaticModel(meshIDs);

        return context.addNode(
            ImmutableNode.builder()
                .name(model.nameOpt())
                .children(nodeIDs)
                .build());
    }

    private List<NodeID> mapStaticModel(List<MeshID> meshIDs) {
        return meshIDs.stream()
            .map(meshID -> context.addNode(ImmutableNode.builder()
                .mesh(meshID)
                .build()))
            .toList();
    }

    private List<NodeID> mapAnimatedModel(List<MeshID> meshIDs, Skeleton skeleton) throws IOException {
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
        for (Mesh mesh : model.meshes()) {
            meshSchemas.add(mapMesh(mesh));
        }
        return meshSchemas;
    }

    private MeshSchema mapMesh(Mesh mesh) throws IOException {
        var primitiveSchema = mapMeshPrimitive(mesh);

        return ImmutableMesh.builder()
            .name(mesh.nameOpt())
            .addPrimitives(primitiveSchema)
            .build();
    }
}
