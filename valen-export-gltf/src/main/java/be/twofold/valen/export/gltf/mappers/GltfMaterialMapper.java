package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.extension.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.texture.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

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
        var pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (var reference : material.textures()) {
            switch (reference.type()) {
                case Albedo -> pbrBuilder.baseColorTexture(textureSchema(textureMapper.map(reference)));
                case Emissive -> builder.emissiveTexture(textureSchema(textureMapper.map(reference)));
                case Normal -> builder.normalTexture(normalTextureInfoSchema(textureMapper.map(reference)));
            }
        }

        var groups = material.textures().stream()
            .collect(Collectors.groupingBy(TextureReference::type));

        if (groups.containsKey(TextureType.Specular)) {
            var specular = groups.get(TextureType.Specular).getFirst();
            mapSpecular(specular, builder, pbrBuilder);
        }

        if (groups.containsKey(TextureType.Smoothness)) {
            var smoothness = groups.get(TextureType.Smoothness).getFirst();
            var smoothnessTexture = TextureConverter.convert(smoothness.supplier().get().firstOnly(), TextureFormat.R8_UNORM);
            var metalRoughnessTexture = mapSmoothness(smoothnessTexture);
            var roughnessReference = new TextureReference(smoothness.name(), smoothness.type(), () -> metalRoughnessTexture);

            pbrBuilder.metallicRoughnessTexture(textureSchema(textureMapper.map(roughnessReference)));
        }

        var materialSchema = builder
            .pbrMetallicRoughness(pbrBuilder.build())
            .build();

        var materialID = context.addMaterial(materialSchema);
        materials.put(material.name(), materialID);
        return materialID;
    }

    private void mapSpecular(
        TextureReference reference,
        MaterialSchema.Builder builder,
        PbrMetallicRoughnessSchema.Builder pbrBuilder
    ) throws IOException {
        var texture = reference.supplier().get().firstOnly();
        var specularSchemaBuilder = KHRMaterialsSpecularSchema.builder();
        if (/*texture.scale() == 0*/false) {
            specularSchemaBuilder.specularFactor(texture.bias());
        } else {
            var specularTexture = TextureConverter.convert(texture, TextureFormat.R8G8B8A8_UNORM);
            var specularReference = new TextureReference(reference.name(), reference.type(), () -> specularTexture);
            specularSchemaBuilder
                .specularColorTexture(textureSchema(textureMapper.map(specularReference)));
        }

        // Workaround for specular in metal-rough
        //  - Set metallic to 0
        //  - Set IOR to 0 or a huge value
        //  - Set specular
        pbrBuilder.metallicFactor(0);

        var iorSchema = KHRMaterialsIORSchema.builder().ior(1000).build();
        builder.putExtensions(iorSchema.getName(), iorSchema);

        var specularSchema = specularSchemaBuilder.build();
        builder.putExtensions(specularSchema.getName(), specularSchema);
    }

    private Texture mapSmoothness(Texture texture) {
        var surface = Surface.create(texture.width(), texture.height(), TextureFormat.R8G8B8A8_UNORM);

        var src = texture.surfaces().getFirst().data();
        var dst = surface.data();

        for (int i = 0, o = 0; i < src.length; i++, o += 4) {
            dst[o + 1] = (byte) (255 - Byte.toUnsignedInt(src[i]));
            dst[o + 3] = (byte) (255);
        }

        return Texture.fromSurface(surface, texture.scale(), texture.bias());
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
