package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.mesh.*;

import java.io.*;
import java.util.*;

public final class GltfModelSingleMapper extends GltfModelMapper {
    private final Map<String, MeshID> models = new HashMap<>();

    public GltfModelSingleMapper(GltfContext context) {
        super(context);
    }

    public MeshID map(ModelReference modelReference) throws IOException {
        var existingNodeID = models.get(modelReference.name());
        if (existingNodeID != null) {
            return existingNodeID;
        }

        var model = modelReference.supplier().get();
        if (model.skeleton() != null) {
            System.out.println("Skipping skeleton for scene on " + model.name());
            model = new Model(model.name(), model.meshes(), null);
        }

        var meshID = mapModel(model);
        models.put(modelReference.name(), meshID);
        return meshID;
    }

    private MeshID mapModel(Model model) throws IOException {
        var primitiveSchemas = new ArrayList<MeshPrimitiveSchema>();
        for (var mesh : model.meshes()) {
            primitiveSchemas.add(mapMeshPrimitive(mesh));
        }

        var meshSchema = MeshSchema.builder()
            .name(Optional.ofNullable(model.name()))
            .addAllPrimitives(primitiveSchemas)
            .build();
        return context.addMesh(meshSchema);
    }

}
