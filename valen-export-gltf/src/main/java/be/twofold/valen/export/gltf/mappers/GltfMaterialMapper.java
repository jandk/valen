package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.texture.*;

import java.io.*;

public final class GltfMaterialMapper {
    private final GltfTextureMapper textureMapper;
    private final GltfContext context;

    public GltfMaterialMapper(GltfContext context) {
        this.context = context;
        this.textureMapper = new GltfTextureMapper(context);
    }

    public MaterialSchema map(Material material) throws IOException {
        var builder = MaterialSchema.builder().name(material.name());
        var pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (var reference : material.textures()) {
            var texture = textureMapper.map(reference);
            var textureID = context.addTexture(texture);

            switch (reference.type()) {
                case Albedo -> pbrBuilder.baseColorTexture(textureSchema(textureID));
                case Normal -> builder.normalTexture(normalTextureInfoSchema(textureID));
                // case Smoothness -> pbrBuilder.metallicRoughnessTexture(textureSchema(textureID));
            }
        }

        return builder
            .pbrMetallicRoughness(pbrBuilder.build())
            .build();
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
