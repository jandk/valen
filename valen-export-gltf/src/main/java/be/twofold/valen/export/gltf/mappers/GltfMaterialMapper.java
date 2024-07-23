package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.*;

public final class GltfMaterialMapper {
    private final GltfContext context;

    public GltfMaterialMapper(GltfContext context) {
        this.context = context;
    }

    MaterialSchema map(Material material) {
        var builder = MaterialSchema.builder().name(material.name());
        var pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (var texture : material.textures()) {
            switch (texture.type()) {
                case Albedo -> {
                    var textureId = context.allocateTextureId(texture.filename());
                    pbrBuilder.baseColorTexture(TextureInfoSchema.builder().index(textureId).build());
                }
                case Normal -> {
                    var textureId = context.allocateTextureId(texture.filename());
                    builder.normalTexture(NormalTextureInfoSchema.builder().index(textureId).build());
                }
                case Smoothness -> {
                    var textureId = context.allocateTextureId(texture.filename());
                    pbrBuilder.metallicRoughnessTexture(TextureInfoSchema.builder().index(textureId).build());
                }
            }
        }
        return builder
            .pbrMetallicRoughness(pbrBuilder.build())
            .build();
    }
}
