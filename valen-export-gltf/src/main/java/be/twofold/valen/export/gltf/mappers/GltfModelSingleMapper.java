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

    public Optional<MeshID> map(ModelReference modelReference) throws IOException {
        if (models.containsKey(modelReference.name())) {
            return Optional.ofNullable(models.get(modelReference.name()));
        }

        var model = modelReference.supplier().get();
        if (model.meshes().isEmpty()) {
            System.out.println("Skipping model without meshes: " + modelReference.name());
            models.put(modelReference.name(), null);
            return Optional.empty();
        }

        if (model.skeleton() != null) {
            System.out.println("Skipping skeleton for scene on " + model.name());
            model = new Model(model.name(), model.meshes(), null);
        }

        var meshID = mapModel(model);
        models.put(modelReference.name(), meshID);
        return Optional.of(meshID);
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
