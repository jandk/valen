package org.redeye.valen.game.spacemarines2.converters;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.psSection.*;

import java.io.*;
import java.util.*;

public class MaterialConverter {
    public ArrayList<Material> convertMaterials(Archive archive, EmperorAssetId resourceId) throws IOException {
        Map<String, ?> resInfo = (Map<String, ?>) archive.loadAsset(resourceId);
        var materials = new ArrayList<Material>();
        for (String materialLink : ((List<String>) resInfo.get("linksTd"))) {
            System.out.println("Exporting " + materialLink);
            Map<String, Object> matResourceInfo = (Map<String, Object>) archive.loadAsset(new EmperorAssetId(materialLink.substring(6)));
            var textureRefs = new ArrayList<TextureReference>();
            List<String> get = (List<String>) matResourceInfo.get("linksPct");
            boolean useAlpha = false;
            for (String textureLink : get) {
                String tdFilePath = "td" + textureLink.substring(9, textureLink.length() - 13) + ".td";
                PsSectionValue.PsSectionObject tdData = (PsSectionValue.PsSectionObject) archive.loadAsset(new EmperorAssetId(tdFilePath));

                String usageInfo;
                if (tdData.has("convert_settings")) {
                    usageInfo = tdData.get("convert_settings").asObject().get("format_descr").asString();
                } else {
                    usageInfo = "Undefined";
                }
                var outName = textureLink.substring(10, textureLink.length() - 13);
                switch (tdData.get("usage").asString()) {
                    case "MD", "" -> {
                        if (outName.endsWith("_dm") || outName.endsWith("_diffdet") || outName.endsWith("_det") || outName.startsWith("gradient_")) {
                            textureRefs.add(new TextureReference(outName, TextureType.Unknown, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                        } else {
                            textureRefs.add(new TextureReference(outName, TextureType.Albedo, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                        }
                    }
                    case "MD+MAK" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.Albedo, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                        useAlpha = true;
                    }
                    case "MD+MRGH" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.Albedo, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                    }
                    case "MD+MT" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.Albedo, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                        useAlpha = false;
                    }
                    case "MDTM" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.DetailMask, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                    }
                    case "MEM" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.Emissive, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                    }
                    case "MH" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.Height, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                    }
                    case "MAO" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.AmbientOcclusion, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                    }
                    case "MD+MSP" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.Albedo, () -> (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)))));
                    }
                    case "MNM" -> {

                        textureRefs.add(new TextureReference(outName, TextureType.Normal, () -> {
                            var texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
                            var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                            var data = converted.data();
                            for (int p = 0; p < converted.data().length; p += 4) {
                                data[p + 1] = (byte) (255 - data[p + 1]);
                            }
                            return new Texture(converted.width(),
                                converted.height(),
                                converted.format(),
                                List.of(converted),
                                false
                            );
                        }));
                    }
                    case "MDT" -> {
                        textureRefs.add(new TextureReference(outName, TextureType.DetailNormal, () -> {
                            var texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
                            var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                            var data = converted.data();
                            for (int p = 0; p < converted.data().length; p += 4) {
                                data[p + 1] = (byte) (255 - data[p + 1]);
                            }
                            return new Texture(converted.width(),
                                converted.height(),
                                converted.format(),
                                List.of(converted),
                                false
                            );
                        }));
                    }
                    case "MSCRGHAO" -> {

                        textureRefs.add(new TextureReference(outName, TextureType.ORM, () -> {
                            var texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
                            var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                            var data = converted.data();
                            for (int p = 0; p < converted.data().length; p += 4) {
                                var tmp = data[p];
                                data[p] = data[p + 2];
                                data[p + 2] = tmp;
                            }
                            return new Texture(converted.width(),
                                converted.height(),
                                converted.format(),
                                List.of(converted),
                                false
                            );
                        }));
                    }
                    case "MEM+MAO" -> {
                        var texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
                        var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.data();
                        var emissiveSurface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var emissiveData = emissiveSurface.data();
                        var aoSurface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var aoData = emissiveSurface.data();
                        for (int p = 0; p < converted.data().length; p += 4) {
                            emissiveData[p] = data[p];
                            emissiveData[p + 1] = data[p + 1];
                            emissiveData[p + 2] = data[p + 2];
                            emissiveData[p + 3] = -1;
                            aoData[p] = data[p + 3];
                            aoData[p + 1] = data[p + 3];
                            aoData[p + 2] = data[p + 3];
                            aoData[p + 3] = -1;
                        }
                        var emissiveTexture = new Texture(emissiveSurface.width(),
                            emissiveSurface.height(),
                            emissiveSurface.format(),
                            List.of(emissiveSurface),
                            false
                        );
                        var aoTexture = new Texture(aoSurface.width(),
                            aoSurface.height(),
                            aoSurface.format(),
                            List.of(aoSurface),
                            false
                        );
                        textureRefs.add(new TextureReference(outName, TextureType.Emissive, () -> emissiveTexture));
                        textureRefs.add(new TextureReference(outName, TextureType.AmbientOcclusion, () -> aoTexture));
                    }
                    case "MH+MDTM" -> {
                        var texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
                        var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.data();
                        var heightSurface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var heightData = heightSurface.data();
                        var detailMaskSurface = Surface.create(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM);
                        var detailMaskData = heightSurface.data();
                        for (int p = 0; p < converted.data().length; p += 4) {
                            heightData[p] = data[p];
                            heightData[p + 1] = data[p];
                            heightData[p + 2] = data[p];
                            heightData[p + 3] = -1;
                            detailMaskData[p] = data[p + 1];
                            detailMaskData[p + 1] = data[p + 1];
                            detailMaskData[p + 2] = data[p + 1];
                            detailMaskData[p + 3] = -1;
                        }
                        var heightTexture = new Texture(heightSurface.width(),
                            heightSurface.height(),
                            heightSurface.format(),
                            List.of(heightSurface),
                            false
                        );
                        var detailMaskTexture = new Texture(detailMaskSurface.width(),
                            detailMaskSurface.height(),
                            detailMaskSurface.format(),
                            List.of(detailMaskSurface),
                            false
                        );
                        textureRefs.add(new TextureReference(outName, TextureType.Height, () -> heightTexture));
                        textureRefs.add(new TextureReference(outName, TextureType.DetailMask, () -> detailMaskTexture));
                    }
                    case "MSCG+MRGH" -> {
                        var texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
                        var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.data();
                        var newData = new byte[TextureFormat.R8G8B8A8_UNORM.block().surfaceSize(converted.width(), converted.height())];
                        for (int p = 0; p < converted.data().length / 4; p++) {
                            newData[p * 4 + 1] = data[p * 4 + 1];
                            newData[p * 4 + 2] = data[p * 4 + 2];
                        }
                        var newTexture = new Texture(converted.width(),
                            converted.height(),
                            converted.format(),
                            List.of(new Surface(converted.width(), converted.height(), TextureFormat.R8G8B8A8_UNORM, newData)),
                            false
                        );
                        textureRefs.add(new TextureReference(outName, TextureType.ORM, () -> newTexture));
                    }
                    default ->
                        throw new IllegalStateException("Unexpected value: \"%s\"(%s) in %s".formatted(tdData.get("usage").asString(), usageInfo, tdFilePath));
                }
            }
            materials.add(new Material(((String) matResourceInfo.get("name")), textureRefs, useAlpha));
        }
        return materials;
    }
}
