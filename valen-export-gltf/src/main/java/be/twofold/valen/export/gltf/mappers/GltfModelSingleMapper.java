package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.mesh.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GltfModelSingleMapper extends GltfModelMapper {
    private static final Logger log = LoggerFactory.getLogger(GltfModelSingleMapper.class);

    private final Map<String, MeshID> models = new HashMap<>();

    public GltfModelSingleMapper(GltfContext context, Path exportPath) {
        super(context);
    }

    public Optional<MeshID> map(ModelReference modelReference) throws IOException {
        if (models.containsKey(modelReference.name())) {
            return Optional.ofNullable(models.get(modelReference.name()));
        }

        var model = modelReference.supplier().get();
        if (model.meshes().isEmpty()) {
            log.warn("Skipping model without meshes {}", modelReference.name());
            models.put(modelReference.name(), null);
            return Optional.empty();
        }

        if (model.skeleton().isPresent()) {
            log.warn("Skipping skeleton for scene on {}", model.name().orElse(""));
            model = model.withSkeleton(Optional.empty());
        }

        var meshID = mapModel(model);
        models.put(modelReference.name(), meshID);
        return Optional.of(meshID);
    }

    private MeshID mapModel(Model model) throws IOException {
        var primitiveSchemas = new ArrayList<MeshPrimitiveSchema>();
        for (var mesh : model.meshes()) {
            mapMeshPrimitive(mesh).ifPresent(primitiveSchemas::add);
        }

        var meshSchema = ImmutableMesh.builder()
            .name(model.name())
            .primitives(primitiveSchemas)
            .build();
        return context.addMesh(meshSchema);
    }
}
