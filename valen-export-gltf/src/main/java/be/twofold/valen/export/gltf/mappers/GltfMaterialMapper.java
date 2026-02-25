package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.extension.*;
import be.twofold.valen.format.gltf.model.material.*;
import be.twofold.valen.format.gltf.model.texture.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
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
        if (material == null) {
            return null;
        }
        var existingMaterialID = materials.get(material.name());
        if (existingMaterialID != null) {
            return existingMaterialID;
        }

        var builder = ImmutableMaterial.builder().name(material.name());
        var pbrBuilder = ImmutableMaterialPbrMetallicRoughness.builder();
        for (var property : material.properties()) {
            switch (property.type()) {
                case Albedo -> mapProperty(property, pbrBuilder::baseColorTexture,
                    v -> pbrBuilder.baseColorFactor(GltfUtils.mapVector4(v)));
                case Normal -> builder.normalTexture(normalTextureInfoSchema(textureMapper.map(property.reference())));
                case Occlusion -> builder.occlusionTexture(occlusionTextureInfoSchema(property));
                case Emissive -> mapEmissive(property, builder);
                case Unknown -> textureMapper.map(property.reference());
            }
        }

        var groups = material.properties().stream()
            .collect(Collectors.groupingBy(MaterialProperty::type));

        if (groups.containsKey(MaterialPropertyType.Specular)) {
            var specular = groups.get(MaterialPropertyType.Specular).getFirst();
            mapSpecular(specular, builder, pbrBuilder);
        }

        // TODO: Check this code
        if (groups.containsKey(MaterialPropertyType.Smoothness)) {
            var property = groups.get(MaterialPropertyType.Smoothness).getFirst();
            var reference = property.reference();
            var smoothnessTexture = reference.supplier().get().firstOnly().convert(TextureFormat.R8_UNORM, true);
            var metalRoughnessTexture = mapSmoothness(smoothnessTexture);
            var roughnessReference = new TextureReference(reference.name(), reference.filename() + ".mr", () -> metalRoughnessTexture);

            // TODO: Proper support for metallic and roughness factors
            var roughnessTexture = textureMapper.map(roughnessReference);
            pbrBuilder.metallicRoughnessTexture(textureSchema(roughnessTexture));
        }

        var materialSchema = builder
            .pbrMetallicRoughness(pbrBuilder.build())
            .build();

        var materialID = context.addMaterial(materialSchema);
        materials.put(material.name(), materialID);
        return materialID;
    }

    private void mapEmissive(
        MaterialProperty property,
        ImmutableMaterial.Builder builder
    ) throws IOException {
        var emissiveFactor = new Vector4(property.factor().xyz(), 1.0f);
        mapProperty(property.withFactor(emissiveFactor), builder::emissiveTexture,
            v -> builder.emissiveFactor(GltfUtils.mapVector3(v.xyz())));

        var emissiveScale = property.factor().w();
        if (emissiveScale != 1.0f) {
            var emissiveStrengthSchema = ImmutableKHRMaterialsEmissiveStrength.builder()
                .emissiveStrength(emissiveScale)
                .build();

            builder.putExtension(emissiveStrengthSchema.getName(), emissiveStrengthSchema);
        }
    }

    private void mapSpecular(
        MaterialProperty property,
        ImmutableMaterial.Builder builder,
        ImmutableMaterialPbrMetallicRoughness.Builder pbrBuilder
    ) throws IOException {
        var specularBuilder = ImmutableKHRMaterialsSpecular.builder();

        mapProperty(property, specularBuilder::specularColorTexture,
            v -> specularBuilder.specularColorFactor(GltfUtils.mapVector3(v.xyz())));

        // Workaround for specular in metal-rough
        //  - Set metallic to 0
        //  - Set IOR to 0 or a huge value
        //  - Set specular
        pbrBuilder.metallicFactor(0);

        var iorSchema = ImmutableKHRMaterialsIor.builder().ior(1000).build();
        builder.putExtension(iorSchema.getName(), iorSchema);

        var specularSchema = specularBuilder.build();
        builder.putExtension(specularSchema.getName(), specularSchema);
    }

    private void mapProperty(
        MaterialProperty property,
        Consumer<TextureInfoSchema> textureConsumer,
        Consumer<Vector4> factorConsumer
    ) throws IOException {
        var factor = property.factor() != null ? property.factor() : Vector4.ONE;
        var textureID = (TextureID) null;
        if (property.reference() != null) {
            textureID = textureMapper.map(property.reference());
        }

        if (textureID != null) {
            textureConsumer.accept(textureSchema(textureID));
        }

        // The default in GLTF is 0, 0, 0 for emissive
        var reference = property.type() == MaterialPropertyType.Emissive ? Vector4.W : Vector4.ONE;
        /*if (!reference.equals(factor)) */
        {
            factorConsumer.accept(factor);
        }
    }

    private Texture mapSmoothness(Texture texture) {
        var surface = Surface.create(texture.width(), texture.height(), TextureFormat.R8G8B8A8_UNORM);

        var src = texture.surfaces().getFirst().data();
        var dst = surface.data();

        for (int i = 0, o = 0; i < src.length; i++, o += 4) {
            dst[o + 1] = (byte) (255 - Byte.toUnsignedInt(src[i]));
            dst[o + 3] = (byte) (255);
        }

        return Texture.fromSurface(surface, TextureFormat.R8G8B8A8_UNORM, texture.scale(), texture.bias());
    }

    private TextureInfoSchema textureSchema(TextureID textureID) {
        return ImmutableTextureInfo.builder()
            .index(textureID)
            .build();
    }

    private MaterialNormalTextureInfoSchema normalTextureInfoSchema(TextureID textureID) {
        return ImmutableMaterialNormalTextureInfo.builder()
            .index(textureID)
            .build();
    }

    private Optional<MaterialOcclusionTextureInfoSchema> occlusionTextureInfoSchema(MaterialProperty property) throws IOException {
        if (property.reference() == null) {
            return Optional.empty();
        }

        var builder = ImmutableMaterialOcclusionTextureInfo.builder()
            .index(textureMapper.map(property.reference()));
        if (property.factor() != null) {
            builder.strength(property.factor().w());
        }

        return Optional.of(builder.build());
    }
}
