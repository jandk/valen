package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.serializers.tpl.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public class TPLResource implements Reader<List<Model>> {
    @Override
    public List<Model> read(Archive archive, Asset asset, DataSource source) throws IOException {
        if (!(asset.id() instanceof EmperorAssetId emperorAssetId)) {
            return null;
        }
        ResourceHeader header = ResourceHeader.read(source);
        AnimTplSerializer serializer = new AnimTplSerializer();
        AnimTemplate animTemplate = serializer.load(source);

        if (animTemplate.geometryManager == null) {
            return null;
        }
        var geometryManager = animTemplate.geometryManager;

        if (geometryManager.rootObjId != null) {
            ObjObj rootObj = geometryManager.objects.get(geometryManager.rootObjId);
            // Check.state(rootObj.name == null);
        }

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

        var skeleton = new Skeleton(bones);

        ByteBuffer streamData = null;
        if (archive.exists(emperorAssetId.withExt(".tpl_data"))) {
            streamData = archive.loadRawAsset(emperorAssetId.withExt(".tpl_data"));
        }

        var streams = geometryManager.streams;
        for (int streamId = 0; streamId < geometryManager.geomSetsInfo.getStreamRefs().size(); streamId++) {
            var streamRef = geometryManager.geomSetsInfo.getStreamRefs().get(streamId);
            var stream = streams.get(streamId);
            if ((stream.state & 2) == 0) {
                Check.state(streamData != null);
                Check.state(streamRef.getSize() == stream.size.longValue());
                stream.data = new byte[Math.toIntExact(streamRef.getSize())];
                streamData.get(Math.toIntExact(streamRef.getOffset()), stream.data, 0, Math.toIntExact(streamRef.getSize()));
                Files.write(Path.of("stream_" + streamId + ".bin"), stream.data);
            }
        }
        List<ObjSplit> splits = geometryManager.splits;

        var models = new ArrayList<Model>();
        var lod0Ids = animTemplate.lodDef != null ? animTemplate.lodDef.stream().filter(lodDef -> lodDef.index == 0).map(lodDef -> lodDef.objId).toList() : null;
        if (geometryManager.objSpitInfo != null) {
            extractBySplitInfo(geometryManager, lod0Ids, splits, streams, models, skeleton);
        } else {
            List<Mesh> meshes = new ArrayList<>();
            List<Material> materials = new ArrayList<>();
            for (int i = 0; i < splits.size(); i++) {
                convertSplitMesh(splits.get(i), streams, i, meshes, materials);
            }
            models.add(new Model("Object", meshes, materials, skeleton));
        }
        return models;
    }

    private void extractBySplitInfo(GeometryManager geometryManager, List<Short> lod0Ids, List<ObjSplit> splits, List<ObjGeomStream> streams, ArrayList<Model> models, Skeleton skeleton) {
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
            List<Mesh> meshes = new ArrayList<>();
            List<Material> materials = new ArrayList<>();
            System.out.println(obj.name);
            for (int i = objSplitRange.startIndex; i < objSplitRange.startIndex + objSplitRange.numSplits; i++) {
                convertSplitMesh(splits.get(i), streams, i, meshes, materials);
            }
            models.add(new Model(obj.name != null ? obj.name : "Object", meshes, materials, skeleton));
        }
    }

    private void convertSplitMesh(ObjSplit split, List<ObjGeomStream> streams, int j, List<Mesh> meshes, List<Material> materials) {
        ObjGeom geom = split.geom;
        System.out.println(split);
        geom.streams.forEach((slot, stream) -> {
            var streamId = streams.indexOf(stream);
            System.out.printf("Stream(%d) %s: stride: %d, %s, %s%n", streamId, slot, stream.stride, stream.fvf, stream.flags);
        });

        Set<FVF> geomFVF = geom.fvf;

        var attributes = buildAttributes(geomFVF, split.nVert);

        geom.streams.forEach((slot, stream) -> {
            if (stream != null && !stream.fvf.isEmpty())
                readStream(stream, geom.streamsOffset.get(slot) + split.startVert * stream.stride, attributes, split.texCoordMaxTile, split.nVert, split.vertCompParams);
        });

        var indicesStreamEntry = geom.streams.entrySet().stream().filter(entry -> entry.getValue() != null && entry.getValue().fvf.isEmpty()).findFirst().orElseThrow();
        ObjGeomStream indicesStream = indicesStreamEntry.getValue();
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
        Check.index(max, split.nVert);
        attributes.values().forEach(vertexBuffer -> vertexBuffer.buffer().rewind());


        String materialName = extractMaterialName(split);
        var matObj = materials.stream().filter(material -> materialName.equals(material.name())).findFirst().orElseGet(() -> new Material(materialName, List.of()));
        if (!materials.contains(matObj)) {
            materials.add(new Material(materialName, List.of()));
        }
        var matId = materials.indexOf(matObj);

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

    private void readStream(ObjGeomStream vertexStream, int offset, Map<Semantic, VertexBuffer> attributes, short[] uvTiles, int vertCount, VertCompressParams compressParams) {
        ByteBuffer inBuf = ByteBuffer.wrap(vertexStream.data, offset, vertexStream.size - offset).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < vertCount; i++) {
            inBuf.position(offset + vertexStream.stride * i);

            Set<FVF> streamFVF = vertexStream.fvf;
            if (streamFVF.contains(FVF.OBJ_FVF_VERT) || streamFVF.contains(FVF.OBJ_FVF_VERT_COMPR)) {
                FloatBuffer buf = (FloatBuffer) attributes.get(Semantic.Position).buffer();
                if (streamFVF.contains(FVF.OBJ_FVF_VERT_COMPR)) {
                    buf.put((((float) inBuf.getShort() / 32768.f) * compressParams.scale[0]) + compressParams.offset[0]);
                    buf.put((((float) inBuf.getShort() / 32768.f) * compressParams.scale[1]) + compressParams.offset[1]);
                    buf.put((((float) inBuf.getShort() / 32768.f) * compressParams.scale[2]) + compressParams.offset[2]);
                } else {
                    buf.put(inBuf.getFloat());
                    buf.put(inBuf.getFloat());
                    buf.put(inBuf.getFloat());
                }
            }
            if (streamFVF.contains(FVF.OBJ_FVF_NORM)) {
                if (streamFVF.contains(FVF.OBJ_FVF_NORM_COMPR)) {
                    FloatBuffer buf = (FloatBuffer) attributes.get(Semantic.Normal).buffer();
                    Vector3 norm = decompressNormalFromInt16(inBuf.getShort());
                    buf.put(norm.x());
                    buf.put(norm.y());
                    buf.put(norm.z());
                } else {
                    FloatBuffer buf = (FloatBuffer) attributes.get(Semantic.Normal).buffer();
                    Vector3 norm = decompressNormalFromFloat(inBuf.getFloat());
                    buf.put(norm.x());
                    buf.put(norm.y());
                    buf.put(norm.z());
                }
            } else if (streamFVF.contains(FVF.OBJ_FVF_NORM_COMPR)) {
                Check.state(false, "Should not reach");
                FloatBuffer buf = (FloatBuffer) attributes.get(Semantic.Normal).buffer();
                buf.put(inBuf.getFloat());
                buf.put(inBuf.getFloat());
                buf.put(inBuf.getFloat());
            }

            if (streamFVF.contains(FVF.OBJ_FVF_WEIGHT8) && streamFVF.contains(FVF.OBJ_FVF_INDICES16)) {
                ByteBuffer bufW = (ByteBuffer) attributes.get(Semantic.Weights0).buffer();

                byte[] weights = new byte[4];
                inBuf.get(weights);

                weights[3] = 0;
                bufW.put(weights);
            }

            if (streamFVF.contains(FVF.OBJ_FVF_INDICES16)) {
                if (streamFVF.contains(FVF.OBJ_FVF_WEIGHT8)) {
                    ShortBuffer buf = (ShortBuffer) attributes.get(Semantic.Joints0).buffer();
                    buf.put((short) Byte.toUnsignedInt(inBuf.get()));
                    buf.put((short) Byte.toUnsignedInt(inBuf.get()));
                    buf.put((short) Byte.toUnsignedInt(inBuf.get()));
                    buf.put((short) Byte.toUnsignedInt(inBuf.get()));
                } else {
                    ShortBuffer bufI0 = (ShortBuffer) attributes.get(Semantic.Joints0).buffer();
                    ShortBuffer bufI1 = (ShortBuffer) attributes.get(Semantic.Joints1).buffer();
                    ByteBuffer bufW0 = (ByteBuffer) attributes.get(Semantic.Weights0).buffer();
                    ByteBuffer bufW1 = (ByteBuffer) attributes.get(Semantic.Weights1).buffer();
                    int[] weights = new int[8];
                    short[] indices = new short[8];

                    for (int j = 0; j < 8; j++) {
                        weights[j] = Byte.toUnsignedInt(inBuf.get());
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
                                int tempWeight = weights[k];
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

            for (FVF tangentLayerFlag : EnumSet.range(FVF.OBJ_FVF_TANG0, FVF.OBJ_FVF_TANG4)) {
                if (streamFVF.contains(tangentLayerFlag)) {
                    if (streamFVF.contains(FVF.OBJ_FVF_TANG_COMPR)) {
                        Check.state(false);
                    }
                    inBuf.getInt();
                }
            }

            int colorLayerCount = 0;
            for (FVF colorLayerFlag : EnumSet.range(FVF.OBJ_FVF_COLOR0, FVF.OBJ_FVF_COLOR5)) {
                if (streamFVF.contains(colorLayerFlag)) {
                    ByteBuffer buf = (ByteBuffer) attributes.get(new Semantic.Color(colorLayerCount)).buffer();
                    buf.put(inBuf.get());
                    buf.put(inBuf.get());
                    buf.put(inBuf.get());
                    buf.put(inBuf.get());
                    colorLayerCount++;
                }
            }

            int uvLayerCount = 0;
            int uvLayerId = 0;
            for (FVF uvLayerFlag : EnumSet.range(FVF.OBJ_FVF_TEX0, FVF.OBJ_FVF_TEX4)) {
                if (streamFVF.contains(uvLayerFlag)) {
                    FloatBuffer buf = (FloatBuffer) attributes.get(new Semantic.TexCoord(uvLayerCount)).buffer();
                    float uvTile = 1;
                    if (streamFVF.contains(FVF.values()[FVF.OBJ_FVF_TEX0_COMPR.ordinal() + uvLayerId])) {
                        uvTile += uvTiles[uvLayerId];
                    }
                    float u = (inBuf.getShort() / 32767.f) / uvTile;
                    float v = (inBuf.getShort() / 32767.f) / uvTile;
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
        if (geomFVF.contains(FVF.OBJ_FVF_VERT) || geomFVF.contains(FVF.OBJ_FVF_VERT_COMPR)) {
            map.put(Semantic.Position,
                new VertexBuffer(FloatBuffer.allocate(vertexCount * 3), ElementType.Vector3, ComponentType.Float, false));
        }
        if (geomFVF.contains(FVF.OBJ_FVF_NORM) || geomFVF.contains(FVF.OBJ_FVF_NORM_COMPR)) {
            map.put(Semantic.Normal,
                new VertexBuffer(FloatBuffer.allocate(vertexCount * 3), ElementType.Vector3, ComponentType.Float, false));
        }
        if (geomFVF.contains(FVF.OBJ_FVF_WEIGHT8)) {
            map.put(Semantic.Weights0,
                new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedByte, true));
        }
        if (geomFVF.contains(FVF.OBJ_FVF_WEIGHT4)) {
            Check.state(false, "Unsupported OBJ_FVF_WEIGHT4");
            // map.put(Semantic.Weights0,
            //     new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.Byte, true));
        }
        if (geomFVF.contains(FVF.OBJ_FVF_INDICES)) {
            Check.state(false, "Unsupported OBJ_FVF_INDICES");
            // map.put(Semantic.Weights0,
            //     new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.Byte, true));
        }
        if (geomFVF.contains(FVF.OBJ_FVF_INDICES16)) {
            if (geomFVF.contains(FVF.OBJ_FVF_WEIGHT8)) {
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
        for (FVF uvLayerFlag : EnumSet.range(FVF.OBJ_FVF_TEX0, FVF.OBJ_FVF_TEX4)) {
            if (geomFVF.contains(uvLayerFlag)) {
                map.put(new Semantic.TexCoord(uvLayerCount),
                    new VertexBuffer(FloatBuffer.allocate(vertexCount * 2), ElementType.Vector2, ComponentType.Float, false));
                uvLayerCount++;
            }
        }

        int colorLayerCount = 0;
        for (FVF colorLayerFlag : EnumSet.range(FVF.OBJ_FVF_COLOR0, FVF.OBJ_FVF_COLOR5)) {
            if (geomFVF.contains(colorLayerFlag)) {
                map.put(new Semantic.Color(colorLayerCount),
                    new VertexBuffer(ByteBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.UnsignedByte, true));
                colorLayerCount++;
            }
        }
        // int tangentLayerCount = 0;
        // for (FVF tangentLayerFlag : EnumSet.range(FVF.OBJ_FVF_TANG0, FVF.OBJ_FVF_TANG4)) {
        //     if (geomFVF.contains(tangentLayerFlag)) {
        //         map.put(new Semantic.Tangent(tangentLayerCount),
        //             new VertexBuffer(FloatBuffer.allocate(vertexCount * 4), ElementType.Vector4, ComponentType.Float, false));
        //         tangentLayerCount++;
        //     }
        // }
        return map;
    }

    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.inferAssetType() == AssetType.Model && id.fileName().endsWith(".tpl");
        }
        return false;
    }
}
