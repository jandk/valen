package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GltfModelMultiMapper extends GltfModelMapper {
    private final Map<String, NodeID> models = new HashMap<>();

    private final GltfContext context;
    private final GltfSkeletonMapper skeletonMapper;

    public GltfModelMultiMapper(GltfContext context, Path exportPath) {
        super(context, exportPath);
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

        var nodeIDs = model.skeletonOpt()
            .map(skeleton -> mapAnimatedModel(meshIDs, skeleton))
            .orElseGet(() -> mapStaticModel(meshIDs));

        return context.addNode(
            NodeSchema.builder()
                .name(model.nameOpt())
                .addAllChildren(nodeIDs)
                .build());
    }

    private List<NodeID> mapStaticModel(List<MeshID> meshIDs) {
        return meshIDs.stream()
            .map(meshID -> context.addNode(NodeSchema.builder()
                .mesh(meshID)
                .build()))
            .toList();
    }

    private List<NodeID> mapAnimatedModel(List<MeshID> meshIDs, Skeleton skeleton) {
        var skinID = skeletonMapper.map(skeleton);

        return meshIDs.stream()
            .map(meshID -> context.addNode(NodeSchema.builder()
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

        return MeshSchema.builder()
            .name(mesh.nameOpt())
            .addPrimitives(primitiveSchema)
            .build();
    }
}
