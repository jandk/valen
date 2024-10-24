package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.texture.*;

import java.io.*;
import java.util.*;

public final class GltfMaterialMapper {
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

        if (material.useAlpha()) {
            builder.alphaMode(MaterialAlphaMode.MASK);
        }

        var pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (var reference : material.textures()) {
            switch (reference.type()) {
                case Albedo -> pbrBuilder.baseColorTexture(textureSchema(textureMapper.map(reference)));
                case Normal -> builder.normalTexture(normalTextureInfoSchema(textureMapper.map(reference)));

                case ORM -> pbrBuilder.metallicRoughnessTexture(textureSchema(textureMapper.map(reference)));
                case Emissive -> builder.emissiveTexture(textureSchema(textureMapper.map(reference)));

                // case Smoothness -> pbrBuilder.metallicRoughnessTexture(textureSchema(textureID));
            }
        }

        var materialSchema = builder
            .pbrMetallicRoughness(pbrBuilder.build())
            .build();

        if (materialSchema.getNormalTexture().isEmpty() || materialSchema.getPbrMetallicRoughness().get().getBaseColorTexture().isEmpty()) {
            System.out.println("Material without textures: " + material.name());
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
