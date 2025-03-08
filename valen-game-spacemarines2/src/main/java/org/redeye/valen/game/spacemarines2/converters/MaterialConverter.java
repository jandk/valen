package org.redeye.valen.game.spacemarines2.converters;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import com.google.gson.*;
import org.redeye.valen.game.spacemarines2.*;

import java.io.*;
import java.util.*;

public class MaterialConverter {
    public ArrayList<Material> convertMaterials(Archive archive, EmperorAssetId resourceId) throws IOException {
        JsonObject resInfo = archive.loadAsset(resourceId, JsonObject.class);
        var materials = new ArrayList<Material>();
        for (JsonElement materialLink : (resInfo.getAsJsonArray("linksTd"))) {
            System.out.println("Exporting " + materialLink);
            JsonObject matResourceInfo = archive.loadAsset(new EmperorAssetId(materialLink.getAsString().substring(6)), JsonObject.class);
            var properties = new ArrayList<MaterialProperty>();
            boolean useAlpha = false;
            for (JsonElement textureLink : matResourceInfo.getAsJsonArray("linksPct")) {
                String textureLinkString = textureLink.getAsString();
                String tdFilePath = "td" + textureLinkString.substring(9, textureLinkString.length() - 13) + ".td";
                JsonObject tdData = archive.loadAsset(new EmperorAssetId(tdFilePath), JsonObject.class);

                String usageInfo;
                if (tdData.has("convert_settings")) {
                    usageInfo = tdData.getAsJsonObject("convert_settings").get("format_descr").getAsString();
                } else {
                    usageInfo = "Undefined";
                }
                var outName = textureLinkString.substring(10, textureLinkString.length() - 13);
                switch (tdData.get("usage").getAsString()) {
                    case "MD", "" -> {
                        if (outName.endsWith("_dm") || outName.endsWith("_diffdet") || outName.endsWith("_det") || outName.startsWith("gradient_")) {
                            MaterialPropertyType type = MaterialPropertyType.Unknown;
                            property(outName, archive, textureLinkString, properties, type);
                        } else {
                            property(outName, archive, textureLinkString, properties, MaterialPropertyType.Albedo);
                        }
                    }
                    case "MD+MRGH" ->
                        property(outName, archive, textureLinkString, properties, MaterialPropertyType.Albedo);
                    case "MDTM" ->
                        property(outName, archive, textureLinkString, properties, MaterialPropertyType.DetailMask);
                    case "MEM" ->
                        property(outName, archive, textureLinkString, properties, MaterialPropertyType.Emissive);
                    case "MH" -> property(outName, archive, textureLinkString, properties, MaterialPropertyType.Height);
                    case "MAO" ->
                        property(outName, archive, textureLinkString, properties, MaterialPropertyType.AmbientOcclusion);
                    case "MD+MSP" ->
                        property(outName, archive, textureLinkString, properties, MaterialPropertyType.Albedo);
                    case "MD+MAK" -> {
                        property(outName, archive, textureLinkString, properties, MaterialPropertyType.Albedo);
                        useAlpha = true;
                    }
                    case "MD+MT" -> {
                        property(outName, archive, textureLinkString, properties, MaterialPropertyType.Albedo);
                        useAlpha = false;
                    }
                    case "MNM" -> {
                        properties.add(new MaterialProperty(MaterialPropertyType.Normal, new TextureReference(outName, outName, () -> {
                            var texture = archive.loadAsset(new EmperorAssetId(textureLinkString.substring(6)), Texture.class);
                            var converted = TextureConverter.convert(texture.firstOnly(), TextureFormat.R8G8B8A8_UNORM);
                            var data = converted.surfaces().getFirst().data();
                            for (int i = 0; i < data.length; i += 4) {
                                data[i + 1] = (byte) (255 - Byte.toUnsignedInt(data[i]));
                            }
                            return converted;
                        })));
                    }
                    case "MDT" -> {
                        properties.add(new MaterialProperty(MaterialPropertyType.DetailNormal, new TextureReference(outName, outName, () -> {
                            var texture = archive.loadAsset(new EmperorAssetId(textureLinkString.substring(6)), Texture.class);
                            var converted = TextureConverter.convert(texture.firstOnly(), TextureFormat.R8G8B8A8_UNORM);
                            var data = converted.surfaces().getFirst().data();
                            for (int i = 0; i < data.length; i += 4) {
                                data[i + 1] = (byte) (255 - Byte.toUnsignedInt(data[i]));
                            }
                            return converted;
                        })));
                    }
                    case "MSCRGHAO" -> {
                        properties.add(new MaterialProperty(MaterialPropertyType.ORM, new TextureReference(outName, outName, () -> {
                            var texture = archive.loadAsset(new EmperorAssetId(textureLinkString.substring(6)), Texture.class);
                            var converted = TextureConverter.convert(texture.firstOnly(), TextureFormat.R8G8B8A8_UNORM);
                            var data = converted.surfaces().getFirst().data();
                            for (int i = 0; i < data.length; i += 4) {
                                var tmp = data[i];
                                data[i] = data[i + 2];
                                data[i + 2] = tmp;
                            }
                            return converted;
                        })));
                    }
                    case "MEM+MAO" -> {
                        var texture = archive.loadAsset(new EmperorAssetId(textureLinkString.substring(6)), Texture.class);
                        var converted = TextureConverter.convert(texture.firstOnly(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.surfaces().getFirst().data();
                        var emissiveSurface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var emissiveData = emissiveSurface.data();
                        var aoSurface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var aoData = emissiveSurface.data();
                        for (int i = 0; i < data.length; i += 4) {
                            emissiveData[i] = data[i];
                            emissiveData[i + 1] = data[i + 1];
                            emissiveData[i + 2] = data[i + 2];
                            emissiveData[i + 3] = -1;
                            aoData[i] = data[i + 3];
                            aoData[i + 1] = data[i + 3];
                            aoData[i + 2] = data[i + 3];
                            aoData[i + 3] = -1;
                        }
                        var emissiveTexture = Texture.fromSurface(emissiveSurface, TextureFormat.R8G8B8A8_UNORM, 1.0f, 0.0f);
                        var aoTexture = Texture.fromSurface(emissiveSurface, TextureFormat.R8G8B8A8_UNORM, 1.0f, 0.0f);
                        properties.add(new MaterialProperty(MaterialPropertyType.Emissive, new TextureReference(outName, outName, () -> emissiveTexture)));
                        properties.add(new MaterialProperty(MaterialPropertyType.AmbientOcclusion, new TextureReference(outName, outName, () -> aoTexture)));
                    }
                    case "MH+MDTM" -> {
                        var texture = archive.loadAsset(new EmperorAssetId(textureLinkString.substring(6)), Texture.class);
                        var converted = TextureConverter.convert(texture.firstOnly(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.surfaces().getFirst().data();
                        var heightSurface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var heightData = heightSurface.data();
                        var detailMaskSurface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var detailMaskData = heightSurface.data();
                        for (int p = 0; p < data.length; p += 4) {
                            heightData[p] = data[p];
                            heightData[p + 1] = data[p];
                            heightData[p + 2] = data[p];
                            heightData[p + 3] = -1;
                            detailMaskData[p] = data[p + 1];
                            detailMaskData[p + 1] = data[p + 1];
                            detailMaskData[p + 2] = data[p + 1];
                            detailMaskData[p + 3] = -1;
                        }
                        var heightTexture = Texture.fromSurface(heightSurface, TextureFormat.R8G8B8A8_UNORM, 1.0f, 0.0f);
                        var detailMaskTexture = Texture.fromSurface(detailMaskSurface, TextureFormat.R8G8B8A8_UNORM, 1.0f, 0.0f);
                        properties.add(new MaterialProperty(MaterialPropertyType.Height, new TextureReference(outName, outName, () -> heightTexture)));
                        properties.add(new MaterialProperty(MaterialPropertyType.DetailMask, new TextureReference(outName, outName, () -> detailMaskTexture)));
                    }
                    case "MSCG+MRGH" -> {
                        var texture = archive.loadAsset(new EmperorAssetId(textureLinkString.substring(6)), Texture.class);
                        var converted = TextureConverter.convert(texture.firstOnly(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.surfaces().getFirst().data();
                        var surface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var newData = surface.data();
                        for (int p = 0; p < data.length / 4; p++) {
                            newData[p * 4 + 1] = data[p * 4 + 1];
                            newData[p * 4 + 2] = data[p * 4 + 2];
                        }
                        var newTexture = Texture.fromSurface(surface, TextureFormat.R8G8B8A8_UNORM, 1.0f, 0.0f);
                        properties.add(new MaterialProperty(MaterialPropertyType.ORM, new TextureReference(outName, outName, () -> newTexture)));
                    }
                    default ->
                        throw new IllegalStateException("Unexpected value: \"%s\"(%s) in %s".formatted(tdData.get("usage").getAsString(), usageInfo, tdFilePath));
                }
            }
            materials.add(new Material((matResourceInfo.get("name").getAsString()), properties, useAlpha));
        }
        return materials;
    }

    private static void property(String outName, Archive archive, String textureLinkString, List<MaterialProperty> properties, MaterialPropertyType type) {
        var reference = new TextureReference(outName, outName, () -> archive.loadAsset(new EmperorAssetId(textureLinkString.substring(6)), Texture.class));
        properties.add(new MaterialProperty(type, reference));
    }
}
