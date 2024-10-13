package org.redeye.valen.game.spacemarines2.converters;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.psSection.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

public class GeometryManagerToModel {


    public Model convert(Archive archive, EmperorAssetId assetId, EmperorAssetId resourceId, GeometryManager geometryManager, List<LodDef> lodDef) throws IOException {
        ArrayList<Material> materials = convertMaterials(archive, resourceId);
        // ArrayList<Material> materials = new ArrayList<>();

        var bones = new ArrayList<Bone>();
        var boneMap = new HashMap<Integer, Integer>();

        for (int i = 0; i < geometryManager.rootObjId; i++) {
            var obj = geometryManager.objects.get(i);
            var parentId = obj.parentId;
            if (obj.parentId >= geometryManager.rootObjId) {
                parentId = -1;
            }
            if (obj.name == null) {
                obj.name = "BONE_" + i;
            }
            var mat = obj.modelMatrix;
            var bone = new Bone(obj.name, parentId, mat.rotation(), mat.scale(), mat.translation(), obj.matrixLt.inverse());
            bones.add(bone);
            boneMap.put(i, bones.indexOf(bone));
        }
        Skeleton skeleton = null;
        if (!bones.isEmpty()) {
            skeleton = new Skeleton(bones);
        }

        var streams = geometryManager.streams;
        List<ObjSplit> splits = geometryManager.splits;

        var subModels = new ArrayList<SubModel>();
        var lod0Ids = lodDef != null ? lodDef.stream().filter(ld -> ld.index == 0).map(ld -> ld.objId).toList() : null;
        if (geometryManager.objSpitInfo != null) {
            extractBySplitInfo(geometryManager, lod0Ids, splits, streams, subModels, materials);
        } else {
            var meshes = new ArrayList<Mesh>();
            for (ObjSplit split : splits) {
                convertSplitMesh(split, streams, meshes, materials);
            }
            subModels.add(new SubModel("Mesh", meshes));
        }
        String modelName = assetId.fileName().substring(0, assetId.fileName().indexOf('.'));
        return new Model(modelName, subModels, materials, skeleton);
    }


    private ArrayList<Material> convertMaterials(Archive archive, EmperorAssetId resourceId) throws IOException {
        Map<String, Map> resInfo = (Map<String, Map>) archive.loadAsset(resourceId);
        var materials = new ArrayList<Material>();
        for (String materialLink : ((List<String>) resInfo.get("linksTd"))) {
            System.out.println("Exporting " + materialLink);
            Map<String, Object> matResourceInfo = (Map<String, Object>) archive.loadAsset(new EmperorAssetId(materialLink.substring(6)));
            var textureRefs = new ArrayList<TextureReference>();
            List<String> get = (List<String>) matResourceInfo.get("linksPct");
            boolean useAlpha = false;
            for (int i = 0; i < get.size(); i++) {
                String textureLink = get.get(i);
                String tdFilePath = "td" + textureLink.substring(9, textureLink.length() - 13) + ".td";
                PsSectionValue.PsSectionObject tdData = (PsSectionValue.PsSectionObject) archive.loadAsset(new EmperorAssetId(tdFilePath));
                Texture texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
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
                            textureRefs.add(new TextureReference(TextureType.Unknown, outName, () -> texture));
                        } else {
                            textureRefs.add(new TextureReference(TextureType.Albedo, outName, () -> texture));
                        }
                    }
                    case "MD+MAK" -> {
                        textureRefs.add(new TextureReference(TextureType.Albedo, outName, () -> texture));
                        useAlpha = true;
                    }
                    case "MD+MRGH" -> {
                        textureRefs.add(new TextureReference(TextureType.Albedo, outName, () -> texture));
                    }
                    case "MD+MT" -> {
                        textureRefs.add(new TextureReference(TextureType.Albedo, outName, () -> texture));
                        useAlpha = false;
                    }
                    case "MDTM" -> {
                        textureRefs.add(new TextureReference(TextureType.DetailMask, outName, () -> texture));
                    }
                    case "MEM" -> {
                        textureRefs.add(new TextureReference(TextureType.Emissive, outName, () -> texture));
                    }
                    case "MH" -> {
                        textureRefs.add(new TextureReference(TextureType.Height, outName, () -> texture));
                    }
                    case "MAO" -> {
                        textureRefs.add(new TextureReference(TextureType.AmbientOcclusion, outName, () -> texture));
                    }
                    case "MD+MSP" -> {
                        textureRefs.add(new TextureReference(TextureType.Albedo, outName, () -> texture));
                    }
                    case "MNM" -> {
                        var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.data();
                        for (int p = 0; p < converted.data().length; p += 4) {
                            data[p + 1] = (byte) (255 - data[p + 1]);
                        }
                        var newTexture = new Texture(converted.width(),
                            converted.height(),
                            converted.format(),
                            List.of(converted),
                            false
                        );
                        textureRefs.add(new TextureReference(TextureType.Normal, outName, () -> newTexture));
                    }
                    case "MDT" -> {
                        var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.data();
                        for (int p = 0; p < converted.data().length; p += 4) {
                            data[p + 1] = (byte) (255 - data[p + 1]);
                        }
                        var newTexture = new Texture(converted.width(),
                            converted.height(),
                            converted.format(),
                            List.of(converted),
                            false
                        );
                        textureRefs.add(new TextureReference(TextureType.DetailNormal, outName, () -> newTexture));
                    }
                    case "MSCRGHAO" -> {
                        var converted = SurfaceConverter.convert(texture.surfaces().getFirst(), TextureFormat.R8G8B8A8_UNORM);
                        var data = converted.data();
                        for (int p = 0; p < converted.data().length; p += 4) {
                            var tmp = data[p];
                            data[p] = data[p + 2];
                            data[p + 2] = tmp;
                        }
                        var newTexture = new Texture(converted.width(),
                            converted.height(),
                            converted.format(),
                            List.of(converted),
                            false
                        );
                        textureRefs.add(new TextureReference(TextureType.ORM, outName, () -> newTexture));
                    }
                    case "MEM+MAO" -> {
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
                        textureRefs.add(new TextureReference(TextureType.Emissive, outName, () -> emissiveTexture));
                        textureRefs.add(new TextureReference(TextureType.AmbientOcclusion, outName, () -> aoTexture));
                    }
                    case "MH+MDTM" -> {
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
                        textureRefs.add(new TextureReference(TextureType.Height, outName, () -> heightTexture));
                        textureRefs.add(new TextureReference(TextureType.DetailMask, outName, () -> detailMaskTexture));
                    }
                    case "MSCG+MRGH" -> {
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
                        textureRefs.add(new TextureReference(TextureType.ORM, outName, () -> newTexture));
                    }
                    default ->
                        throw new IllegalStateException("Unexpected value: \"%s\"(%s) in %s".formatted(tdData.get("usage").asString(), usageInfo, tdFilePath));
                }
            }
            materials.add(new Material(((String) matResourceInfo.get("name")), textureRefs, useAlpha));
        }
        return materials;
    }

    private void extractBySplitInfo(GeometryManager geometryManager, List<Short> lod0Ids, List<ObjSplit> splits, List<ObjGeomStream> streams, ArrayList<SubModel> subModels, ArrayList<Material> materials) {
        List<ObjSplitRange> objSpitInfo = geometryManager.objSpitInfo;
        for (int j = 0; j < objSpitInfo.size(); j++) {
            ObjObj obj = geometryManager.objects.get(j);
            if (lod0Ids != null && !lod0Ids.contains(obj.id)) {
                continue;
            }
            ObjSplitRange objSplitRange = objSpitInfo.get(j);
            if (objSplitRange.numSplits == 0) {
                continue;
            }
            var meshes = new ArrayList<Mesh>();
            for (int i = objSplitRange.startIndex; i < objSplitRange.startIndex + objSplitRange.numSplits; i++) {
                convertSplitMesh(splits.get(i), streams, meshes, materials);
            }
            subModels.add(new SubModel(Objects.requireNonNullElse(obj.name, "SubModel"), meshes));
        }
    }

    private void convertSplitMesh(ObjSplit split, List<ObjGeomStream> streams, ArrayList<Mesh> meshes, ArrayList<Material> materials) {
        ObjGeom geom = split.geom;
        Set<FVF> geomFVF = geom.fvf;

        System.out.println(split);
        geom.streams.forEach((slot, stream) -> {
            System.out.printf("Stream(%d) %s: stride: %d, %s, %s, %s%n", streams.indexOf(stream), slot, stream.stride, stream.fvf, stream.flags, geom.flags);
        });

        String materialName = extractMaterialName(split);
        var matObj = materials.stream().filter(material -> materialName.equals(material.name())).findFirst().orElseGet(() -> new Material(materialName, List.of()));
        if (!materials.contains(matObj)) {
            materials.add(new Material(materialName, List.of()));
        }
        var matId = materials.indexOf(matObj);

        var attributes = buildAttributes(geomFVF, split.nVert);

        geom.streams.forEach((slot, stream) -> {
            if (stream != null && !stream.fvf.isEmpty())
                readStream(stream, geom.streamsOffset.get(slot) + split.startVert * stream.stride, attributes, split.texCoordMaxTile, split.nVert, split.vertCompParams);
        });

        var indicesStreamEntry = geom.streams.entrySet().stream().filter(entry -> entry.getValue() != null && entry.getValue().fvf.isEmpty()).findFirst().orElseThrow();
        ObjGeomStream indicesStream = indicesStreamEntry.getValue();
        if (indicesStream.stride > 6 && indicesStream.stride < 12) {
            System.err.println("Invalid index stream stride: " + indicesStream.stride);
            return;
        }
        ByteBuffer indicesBuffer = ByteBuffer.wrap(indicesStream.data).order(ByteOrder.LITTLE_ENDIAN);
        indicesBuffer.position(split.startFace * indicesStream.stride + (geom.streamsOffset.get(indicesStreamEntry.getKey())));
        short[] indices = new short[split.nFace * 3];
        indicesBuffer.asShortBuffer().get(indices, 0, split.nFace * 3);
        var newIndicesBuffer = ShortBuffer.allocate(split.nFace * 3);
        int max = 0;
        for (short index : indices) {
            int v0 = Short.toUnsignedInt(index) - split.startVert;
            if (v0 > max) {
                max = v0;
            }
            Check.state(v0 >= 0);
            Check.index(v0, 65535);
            newIndicesBuffer.put((short) v0);
        }
        Check.state(max + 1 == split.nVert);
        attributes.values().forEach(vertexBuffer -> vertexBuffer.buffer().rewind());

        Mesh mesh = new Mesh(
            new VertexBuffer(newIndicesBuffer.rewind(), ElementType.Scalar, ComponentType.UnsignedShort, false),
            attributes,
            matId);
        meshes.add(mesh);
    }

    private static String extractMaterialName(ObjSplit split) {
        String materialName;
        String shadingMtlTex = (String) split.materialInfo.get("shadingMtl_Tex");
        if (!shadingMtlTex.isBlank() && !shadingMtlTex.isEmpty()) {
            materialName = shadingMtlTex;
        } else {
            materialName = ((Map<String, Object>) split.materialInfo.get("layer0")).get("texName").toString();
        }
        return materialName;
    }

    public static byte[] renormalize(byte[] byteArray) {
        int sum = 0;
        for (byte b : byteArray) {
            sum += Byte.toUnsignedInt(b);
        }
        double scaleFactor = 255.0 / sum;
        byte[] normalizedArray = new byte[byteArray.length];
        double totalRoundedSum = 0;
        for (int i = 0; i < byteArray.length; i++) {
            double scaledValue = (byteArray[i] & 0xFF) * scaleFactor;
            normalizedArray[i] = (byte) Math.round(scaledValue);
            totalRoundedSum += Byte.toUnsignedInt(normalizedArray[i]);
        }

        int difference = (int) totalRoundedSum - 255;
        if (difference != 0) {
            for (int i = 0; i < byteArray.length; i++) {
                if (difference > 0 && (normalizedArray[i] & 0xFF) > 0) {
                    normalizedArray[i]--;  // Reduce a value to decrease sum
                    difference--;
                } else if (difference < 0 && (normalizedArray[i] & 0xFF) < 255) {
                    normalizedArray[i]++;  // Increase a value to increase sum
                    difference++;
                }
                if (difference == 0) break;
            }
        }

        return normalizedArray;
    }

    private void readStream(ObjGeomStream vertexStream, int offset, Map<Semantic, VertexBuffer> attributes, Map<Integer, Integer> uvTiles, int vertCount, VertCompressParams compressParams) {
        ByteBuffer inBuf = ByteBuffer.wrap(vertexStream.data, offset + vertexStream.vBuffOffset, vertexStream.size - offset).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < vertCount; i++) {
            inBuf.position(offset + vertexStream.stride * i);

            Set<FVF> streamFVF = vertexStream.fvf;
            if (streamFVF.contains(FVF.VERT) || streamFVF.contains(FVF.VERT_COMPR)) {
                FloatBuffer buf = (FloatBuffer) attributes.get(Semantic.Position).buffer();
                if (streamFVF.contains(FVF.VERT_COMPR)) {
                    buf.put((((float) inBuf.getShort() / 32768.f) * compressParams.scale[0]) + compressParams.offset[0]);
                    buf.put((((float) inBuf.getShort() / 32768.f) * compressParams.scale[1]) + compressParams.offset[1]);
                    buf.put((((float) inBuf.getShort() / 32768.f) * compressParams.scale[2]) + compressParams.offset[2]);
                } else {
                    buf.put(inBuf.getFloat());
                    buf.put(inBuf.getFloat());
                    buf.put(inBuf.getFloat());
                }
            }
            if (streamFVF.contains(FVF.NORM)) {
                FloatBuffer buf = (FloatBuffer) attributes.get(Semantic.Normal).buffer();
                if (streamFVF.contains(FVF.NORM_COMPR)) {
                    if (streamFVF.contains(FVF.VERT_COMPR)) {
                        Vector3 norm = decompressNormalFromInt16(inBuf.getShort());
                        buf.put(norm.x());
                        buf.put(norm.y());
                        buf.put(norm.z());
                    } else {
                        Vector3 norm = decompressNormalFromFloat(inBuf.getFloat());
                        buf.put(norm.x());
                        buf.put(norm.y());
                        buf.put(norm.z());
                    }
                } else {
                    buf.put(inBuf.getFloat());
                    buf.put(inBuf.getFloat());
                    buf.put(inBuf.getFloat());
                }
            } else if (streamFVF.contains(FVF.NORM_COMPR)) {
                Check.state(false, "Should not reach");
                FloatBuffer buf = (FloatBuffer) attributes.get(Semantic.Normal).buffer();
                buf.put(inBuf.getFloat());
                buf.put(inBuf.getFloat());
                buf.put(inBuf.getFloat());
            }

            if (streamFVF.contains(FVF.WEIGHT8) && !(streamFVF.contains(FVF.INDICES16) || streamFVF.contains(FVF.INDICES))) {
                ShortBuffer bufI0 = (ShortBuffer) attributes.get(Semantic.Joints0).buffer();
                ByteBuffer bufW0 = (ByteBuffer) attributes.get(Semantic.Weights0).buffer();
                byte[] weights = new byte[4];
                short[] indices = new short[4];

                inBuf.get(weights);
                weights[3] = 0;
                weights = renormalize(weights);
                for (int j = 0; j < 4; j++) {
                    indices[j] = inBuf.getShort();
                }

                for (int j = 0; j < weights.length; j++) {
                    if (weights[j] == 0) {
                        indices[j] = 0;
                    }
                }

                for (int j = 0; j < 4; j++) {
                    bufW0.put(weights[j]);
                    bufI0.put(indices[j]);
                }
            }

            if (streamFVF.contains(FVF.WEIGHT8) && streamFVF.contains(FVF.INDICES16)) {
                ByteBuffer bufW = (ByteBuffer) attributes.get(Semantic.Weights0).buffer();

                byte[] weights = new byte[4];
                inBuf.get(weights);

                weights[3] = 0;

                var normalized = renormalize(weights);

                bufW.put(normalized);
            }

            if (streamFVF.contains(FVF.INDICES16)) {
                if (streamFVF.contains(FVF.WEIGHT8)) {
                    ShortBuffer buf = (ShortBuffer) attributes.get(Semantic.Joints0).buffer();
                    buf.put((short) Byte.toUnsignedInt(inBuf.get()));
                    buf.put((short) Byte.toUnsignedInt(inBuf.get()));
                    buf.put((short) Byte.toUnsignedInt(inBuf.get()));
                    buf.put((short) Byte.toUnsignedInt(inBuf.get()));
                } else {
                    if (vertexStream.stride == 4) {
                        byte[] indices = new byte[4];
                        inBuf.get(indices);

                        var boneCount = IntStream.range(0, indices.length).map(operand -> indices[operand]).distinct().count();

                        byte[] weights = new byte[4];
                        for (int i1 = 0; i1 < boneCount; i1++) {
                            weights[i1] = -1;
                        }
                        weights = renormalize(weights);

                        ShortBuffer buf = (ShortBuffer) attributes.get(Semantic.Joints0).buffer();
                        ByteBuffer bufW = (ByteBuffer) attributes.get(Semantic.Weights0).buffer();
                        for (byte index : indices) {
                            buf.put(index);
                        }
                        bufW.put(weights);
                    } else {


                        ShortBuffer bufI0 = (ShortBuffer) attributes.get(Semantic.Joints0).buffer();
                        ShortBuffer bufI1 = (ShortBuffer) attributes.get(Semantic.Joints1).buffer();
                        ByteBuffer bufW0 = (ByteBuffer) attributes.get(Semantic.Weights0).buffer();
                        ByteBuffer bufW1 = (ByteBuffer) attributes.get(Semantic.Weights1).buffer();
                        float[] weights = new float[8];
                        short[] indices = new short[8];

                        for (int j = 0; j < 8; j++) {
                            weights[j] = Byte.toUnsignedInt(inBuf.get()) / 255.f;
                        }
                        for (int j = 0; j < 8; j++) {
                            indices[j] = (short) Byte.toUnsignedInt(inBuf.get());
                        }

                        float total = 0;
                        for (int j = 0; j < weights.length; j++) {
                            if (weights[j] == 0) {
                                indices[j] = 0;
                            }
                            total += weights[j];
                        }

                        for (int j = 0; j < weights.length - 1; j++) {
                            for (int k = 0; k < weights.length - j - 1; k++) {
                                if (weights[k] < weights[k + 1]) {
                                    float tempWeight = weights[k];
                                    weights[k] = weights[k + 1];
                                    weights[k + 1] = tempWeight;

                                    short tempIndex = indices[k];
                                    indices[k] = indices[k + 1];
                                    indices[k + 1] = tempIndex;
                                }
                            }
                        }

                        for (int j = 0; j < 4; j++) {
                            bufW0.put((byte) ((weights[j] / total) * 255));
                            bufI0.put(indices[j]);
                        }
                        for (int j = 0; j < 4; j++) {
                            bufW1.put((byte) ((weights[j + 4] / total) * 255));
                            bufI1.put(indices[j + 4]);
                        }
                    }
                }
            }

            for (FVF tangentLayerFlag : EnumSet.range(FVF.TANG0, FVF.TANG4)) {
                if (streamFVF.contains(tangentLayerFlag)) {
                    if (streamFVF.contains(FVF.TANG_COMPR)) {
                        Check.state(false);
                    }
                    inBuf.getInt();
                }
            }

            int colorLayerCount = 0;
            for (FVF colorLayerFlag : EnumSet.range(FVF.COLOR0, FVF.COLOR5)) {
                if (streamFVF.contains(colorLayerFlag)) {
                    ByteBuffer buf = (ByteBuffer) attributes.get(new Semantic.Color(colorLayerCount)).buffer();
                    byte[] color = new byte[4];
                    inBuf.get(color);
                    if (color[0] == 0 && color[1] == 0 && color[2] == 0) { // Avoid full black VColors for now
                        color[0] = -1;
                        color[1] = -1;
                        color[2] = -1;
                    }
                    buf.put(color);
                    colorLayerCount++;
                }
            }

            int uvLayerCount = 0;
            int uvLayerId = 0;
            for (FVF uvLayerFlag : EnumSet.range(FVF.TEX0, FVF.TEX4)) {
                if (streamFVF.contains(uvLayerFlag)) {
                    FloatBuffer buf = (FloatBuffer) attributes.get(new Semantic.TexCoord(uvLayerCount)).buffer();
                    float uvTile;
                    if (uvTiles.containsKey(uvLayerId) || streamFVF.contains(FVF.values()[FVF.TEX0_COMPR.ordinal() + uvLayerId])) {
                        uvTile = uvTiles.get(uvLayerId);
                    } else {
                        uvTile = 1;
                    }
                    float u = (inBuf.getShort() / 32767.f); /// uvTile;
                    float v = (inBuf.getShort() / 32767.f); /// uvTile;

                    if (uvTile > 1) {
                        u *= (uvTile);
                        v *= (uvTile);
                    }

                    buf.put(u);
                    buf.put(v);
                    uvLayerCount++;
                }
                uvLayerId++;
            }
        }
    }

    private Vector3 decompressNormalFromInt16(short w) {
        float fracX = (1.0f / 181) * Math.abs(w) % 1.0f;
        float fracZ = (1.0f / (181.0f * 181.0f)) * Math.abs(w) % 1.0f;
        float x = (-1.0f + 2.0f * fracX) * (181.0f / 179.0f);
        float z = (-1.0f + 2.0f * fracZ) * (181.0f / 180.0f);
        float y = (Math.signum(w) * MathF.sqrt(Math.max(0.0f, 1.0f - x * x - z * z)));
        return new Vector3(x, y, z).normalize();
    }

    private Vector3 decompressNormalFromFloat(float w) {
        var x = -1.f + 2.0f * ((w * 0.00390625f) % 1.f);
        var y = -1.f + 2.0f * ((w * 0.0000152587890625f) % 1.f);
        var z = -1.f + 2.0f * ((w * 0.000000059604644775390625f) % 1.f);
        return new Vector3(x, y, z).normalize();
    }

    private Map<Semantic, VertexBuffer> buildAttributes(Set<FVF> geomFVF, int vertexCount) {
        var map = new LinkedHashMap<Semantic, VertexBuffer>();
        if (geomFVF.contains(FVF.VERT) || geomFVF.contains(FVF.VERT_COMPR)) {
            map.put(Semantic.Position,
                new VertexBuffer(FloatBuffer.allocate(vertexCount * 3), ElementType.Vector3, ComponentType.Float, false));
        }
        if (geomFVF.contains(FVF.NORM) || geomFVF.contains(FVF.NORM_COMPR)) {
            map.put(Semantic.Normal,
                new VertexBuffer(FloatBuffer.allocate(vertexCount * 3), ElementType.Vector3, ComponentType.Float, false));
        }

        if (geomFVF.contains(FVF.WEIGHT8) && !(geomFVF.contains(FVF.INDICES16) || geomFVF.contains(FVF.INDICES))) {
            map.put(Semantic.Weights0,
                new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedByte, true));
            map.put(Semantic.Joints0,
                new VertexBuffer(ShortBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedShort, false));
        } else if (geomFVF.contains(FVF.WEIGHT8)) {
            map.put(Semantic.Weights0,
                new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedByte, true));
        }
        if (geomFVF.contains(FVF.WEIGHT4)) {
            Check.state(false, "Unsupported OBJ_FVF_WEIGHT4");
            // map.put(Semantic.Weights0,
            //     new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.Byte, true));
        }
        if (geomFVF.contains(FVF.INDICES)) {
            Check.state(false, "Unsupported OBJ_FVF_INDICES");
            // map.put(Semantic.Weights0,
            //     new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.Byte, true));
        }
        if (geomFVF.contains(FVF.INDICES16)) {
            if (geomFVF.contains(FVF.WEIGHT8)) {
                map.put(Semantic.Joints0,
                    new VertexBuffer(ShortBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedShort, false));
                map.put(Semantic.Weights0,
                    new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedByte, true));
            } else {
                map.put(Semantic.Joints0,
                    new VertexBuffer(ShortBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedShort, false));
                map.put(Semantic.Weights0,
                    new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedByte, true));
                map.put(Semantic.Joints1,
                    new VertexBuffer(ShortBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedShort, false));
                map.put(Semantic.Weights1,
                    new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedByte, true));
            }
        }

        int uvLayerCount = 0;
        for (FVF uvLayerFlag : EnumSet.range(FVF.TEX0, FVF.TEX4)) {
            if (geomFVF.contains(uvLayerFlag)) {
                map.put(new Semantic.TexCoord(uvLayerCount),
                    new VertexBuffer(FloatBuffer.allocate(vertexCount * 2), ElementType.Vector2, ComponentType.Float, false));
                uvLayerCount++;
            }
        }

        int colorLayerCount = 0;
        for (FVF colorLayerFlag : EnumSet.range(FVF.COLOR0, FVF.COLOR5)) {
            if (geomFVF.contains(colorLayerFlag)) {
                map.put(new Semantic.Color(colorLayerCount),
                    new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedByte, true));
                colorLayerCount++;
            }
        }
        // int tangentLayerCount = 0;
        // for (FVF tangentLayerFlag : EnumSet.range(FVF.TANG0, FVF.TANG4)) {
        //     if (geomFVF.contains(tangentLayerFlag)) {
        //         map.put(new Semantic.Tangent(tangentLayerCount),
        //             new VertexBuffer(FloatBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.Float, false));
        //         tangentLayerCount++;
        //     }
        // }
        return map;
    }

}
