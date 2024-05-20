package be.twofold.valen.export.gltf;

import be.twofold.valen.core.material.*;
import be.twofold.valen.export.gltf.model.*;

final class GltfMaterialMapper {
    private final GltfContext context;

    GltfMaterialMapper(GltfContext context) {
        this.context = context;
    }

    MaterialSchema map(Material material) {
        MaterialSchema.Builder builder = MaterialSchema.builder().name(material.name());
        PbrMetallicRoughnessSchema.Builder pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (TextureReference texture : material.textures()) {
            if (texture.type() == TextureType.Albedo) {
                var textureId = context.allocateTextureId(texture.filename());
                pbrBuilder.baseColorTexture(TextureInfoSchema.builder().index(textureId).build());
            } else if (texture.type() == TextureType.Normal) {
                var textureId = context.allocateTextureId(texture.filename());
                builder.normalTexture(NormalTextureInfoSchema.builder().index(textureId).build());
            }
        }
        builder.pbrMetallicRoughness(pbrBuilder.build());
        return builder.build();
    }
}
