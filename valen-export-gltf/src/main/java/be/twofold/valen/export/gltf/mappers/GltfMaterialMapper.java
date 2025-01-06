package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.extension.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.texture.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class GltfMaterialMapper {
    private final Map<String, MaterialID> materials = new HashMap<>();

    private final GltfContext context;
    private final GltfTextureMapper textureMapper;

    public GltfMaterialMapper(GltfContext context, Path exportPath) {
        this.context = context;
        this.textureMapper = new GltfTextureMapper(context, exportPath);
    }

    public MaterialID map(Material material) throws IOException {
        if (material == null) {
            return null;
        }
        var existingMaterialID = materials.get(material.name());
        if (existingMaterialID != null) {
            return existingMaterialID;
        }

        var builder = MaterialSchema.builder().name(material.name());
        var pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (var property : material.properties()) {
            switch (property.type()) {
                case Albedo -> mapProperty(property, pbrBuilder::baseColorTexture,
                    v -> pbrBuilder.baseColorFactor(GltfUtils.mapVector4(v)));
                case Normal ->
                    builder.normalTexture(normalTextureInfoSchema(textureMapper.mapSimple(property.reference())));
                case Emissive -> mapEmissive(property, builder);
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
            var smoothnessTexture = reference.supplier().get().firstOnly().convert(TextureFormat.R8_UNORM);
            var metalRoughnessTexture = mapSmoothness(smoothnessTexture);
            var roughnessReference = new TextureReference(reference.name(), reference.filename(), () -> metalRoughnessTexture);

            // TODO: Proper support for metallic and roughness factors
            var roughnessTexture = textureMapper.mapSimple(roughnessReference);
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
        MaterialSchema.Builder builder
    ) throws IOException {
        var emissiveFactor = new Vector4(property.factor().toVector3(), 1.0f);
        mapProperty(property.withFactor(emissiveFactor), builder::emissiveTexture,
            v -> builder.emissiveFactor(GltfUtils.mapVector3(v.toVector3())));

        var emissiveScale = property.factor().w();
        if (emissiveScale != 1.0f) {
            var emissiveStrengthSchema = KHRMaterialsEmissiveStrengthSchema.builder()
                .emissiveStrength(emissiveScale)
                .build();

            builder.putExtensions(emissiveStrengthSchema.getName(), emissiveStrengthSchema);
        }
    }

    private void mapSpecular(
        MaterialProperty property,
        MaterialSchema.Builder builder,
        PbrMetallicRoughnessSchema.Builder pbrBuilder
    ) throws IOException {
        var specularBuilder = KHRMaterialsSpecularSchema.builder();

        mapProperty(property, specularBuilder::specularColorTexture,
            v -> specularBuilder.specularColorFactor(GltfUtils.mapVector3(v.toVector3())));

        // Workaround for specular in metal-rough
        //  - Set metallic to 0
        //  - Set IOR to 0 or a huge value
        //  - Set specular
        pbrBuilder.metallicFactor(0);

        var iorSchema = KHRMaterialsIORSchema.builder().ior(1000).build();
        builder.putExtensions(iorSchema.getName(), iorSchema);

        var specularSchema = specularBuilder.build();
        builder.putExtensions(specularSchema.getName(), specularSchema);
    }

    private void mapProperty(
        MaterialProperty property,
        Consumer<TextureInfoSchema> textureConsumer,
        Consumer<Vector4> factorConsumer
    ) throws IOException {
        var factor = property.factor() != null ? property.factor() : Vector4.One;
        var textureID = (TextureID) null;
        if (property.reference() != null) {
            var textureIDAndFactor = textureMapper.map(property.reference());
            factor = factor.multiply(textureIDAndFactor.factor());
            textureID = textureIDAndFactor.textureID();
        }

        if (textureID != null) {
            textureConsumer.accept(textureSchema(textureID));
        }

        // The default in GLTF is 0, 0, 0 for emissive
        var reference = property.type() == MaterialPropertyType.Emissive ? Vector4.W : Vector4.One;
        if (!reference.equals(factor)) {
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
