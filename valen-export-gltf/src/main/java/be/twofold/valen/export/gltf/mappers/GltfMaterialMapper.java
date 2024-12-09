package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.op.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.extension.specular.*;
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

        TextureReference roughness = null;
        TextureReference smoothness = null;

        var builder = MaterialSchema.builder().name(material.name());
        var pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (var reference : material.textures()) {
            switch (reference.type()) {
                case Albedo -> pbrBuilder.baseColorTexture(textureSchema(textureMapper.map(reference)));
                case Emissive -> builder.emissiveTexture(textureSchema(textureMapper.map(reference)));
                case Normal -> builder.normalTexture(normalTextureInfoSchema(textureMapper.map(reference)));
                case Smoothness -> roughness = reference;
                case Specular -> {
                    var extension = KHRMaterialsSpecularSchema.builder()
                        .specularColorTexture(textureSchema(textureMapper.map(reference)))
                        .build();
                    builder.putExtensions(extension.getName(), extension);
                }
            }
        }

        // Combine
        U8ChannelOp roughnessOp;
        var width = 0;
        var height = 0;
        if (roughness != null) {
            var surface = roughness.supplier().get().surfaces().getFirst();
            width = surface.width();
            height = surface.height();
            roughnessOp = PixelOp.source(surface).asU8().red();
        } else {
            roughnessOp = U8ChannelOp.constant(0);
        }

        if (width != 0 && height != 0) {
            var metallicRoughness = U8PixelOp.combine(
                U8ChannelOp.constant(0),
                roughnessOp.invert(),
                U8ChannelOp.constant(0),
                U8ChannelOp.constant(255)
            ).toSurface(width, height, TextureFormat.R8G8B8_UNORM);

            var texture = new Texture(width, height, TextureFormat.R8G8B8_UNORM, List.of(metallicRoughness), false);
            var reference = new TextureReference(material.name() + "_mr", TextureType.Unknown, () -> texture);
            pbrBuilder.metallicRoughnessTexture(textureSchema(textureMapper.map(reference)));
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
