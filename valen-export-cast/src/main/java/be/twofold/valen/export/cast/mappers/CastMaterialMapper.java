package be.twofold.valen.export.cast.mappers;

import be.twofold.tinycast.*;
import be.twofold.valen.core.material.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public final class CastMaterialMapper {
    private final Map<String, Long> materials = new HashMap<>();

    private final CastTextureMapper textureMapper;

    public CastMaterialMapper(Path castPath, Path imagePath) {
        this.textureMapper = new CastTextureMapper(castPath, imagePath);
    }

    public Long map(Material material, CastNodes.Model model) throws IOException {
        if (material == null) {
            return null;
        }
        var existingHash = materials.get(material.name());
        if (existingHash != null) {
            return existingHash;
        }

        var materialNode = model.createMaterial()
            .setName(material.name())
            .setType(CastNodes.Type.PBR);
        for (var property : material.properties()) {
            switch (property.type()) {
                case Unknown -> mapProperty(materialNode, property, materialNode::addExtra);
                case Albedo -> mapProperty(materialNode, property, materialNode::setAlbedoHash);
                case Normal -> mapProperty(materialNode, property, materialNode::setNormalHash);
                case Specular -> mapProperty(materialNode, property, materialNode::setSpecularHash);
                case Smoothness -> mapProperty(materialNode, property, materialNode::setGlossHash);
                case Emissive -> mapProperty(materialNode, property, materialNode::setEmissiveHash);
                case Occlusion -> mapProperty(materialNode, property, materialNode::setAmbientOcclusionHash);
            }
        }

        materials.put(material.name(), materialNode.getHash());
        return materialNode.getHash();
    }

    private void mapProperty(CastNodes.Material material, MaterialProperty property, Consumer<Long> setter) throws IOException {
        if (property.reference() != null) {
            setter.accept(textureMapper.map(property.reference(), material));
        } else if (property.factor() != null) {
            setter.accept(mapFactor(material, property.factor()));
        }
    }

    private long mapFactor(CastNodes.Material material, Vector4 color) {
        return material.createColor()
            .setColorSpace(CastNodes.ColorSpace.LINEAR)
            .setRgbaColor(CastUtils.mapVector4(color))
            .getHash();
    }
}
