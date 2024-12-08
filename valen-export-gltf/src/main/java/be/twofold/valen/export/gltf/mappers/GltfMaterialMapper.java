package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.texture.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public final class GltfMaterialMapper {
    private static final Logger log = LoggerFactory.getLogger(GltfMaterialMapper.class);

    private final Map<String, MaterialID> materials = new HashMap<>();

    private final GltfContext context;
    private final GltfTextureMapper textureMapper;

    public GltfMaterialMapper(GltfContext context) {
        this.context = context;
        this.textureMapper = new GltfTextureMapper(context);
    }

    public MaterialID map(Material material) throws IOException {
        var existingMaterialID = materials.get(material.name());
        if (existingMaterialID != null) {
            return existingMaterialID;
        }

        var builder = MaterialSchema.builder().name(material.name());
        var pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (var reference : material.textures()) {
            switch (reference.type()) {
                case Albedo -> pbrBuilder.baseColorTexture(textureSchema(textureMapper.map(reference)));
                case Normal -> builder.normalTexture(normalTextureInfoSchema(textureMapper.map(reference)));
                // case Smoothness -> pbrBuilder.metallicRoughnessTexture(textureSchema(textureMapper.map(reference)));
            }
        }

        var materialSchema = builder
            .pbrMetallicRoughness(pbrBuilder.build())
            .build();

        if (materialSchema.getNormalTexture().isEmpty() || materialSchema.getPbrMetallicRoughness().get().getBaseColorTexture().isEmpty()) {
            log.warn("Material without textures: {}", material.name());
        }

        var materialID = context.addMaterial(materialSchema);
        materials.put(material.name(), materialID);
        return materialID;
    }

    private static TextureInfoSchema textureSchema(TextureID textureID) {
        return TextureInfoSchema.builder()
            .index(textureID)
            .build();
    }

    private static NormalTextureInfoSchema normalTextureInfoSchema(TextureID textureID) {
        return NormalTextureInfoSchema.builder()
            .index(textureID)
            .build();
    }
}
