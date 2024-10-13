package org.redeye.valen.export.dmf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.*;
import be.twofold.valen.export.png.*;
import com.google.gson.*;
import org.redeye.dmf.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.util.*;

public class DmfModelExporter implements Exporter<Model> {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeHierarchyAdapter(DMFBuffer.class, new JsonBufferSerializer())
        .registerTypeAdapter(DMFTransform.class, new JsonTransformSerializer())
        .setPrettyPrinting().create();

    @Override
    public String getExtension() {
        return "dmf";
    }

    @Override
    public Class<Model> getSupportedType() {
        return Model.class;
    }

    @Override
    public void export(Model model, OutputStream out) throws IOException {
        List<Material> materials = model.materials();
        var pngExporter = new PngExporter();

        var scene = new DMFSceneFile(4);

        var modelGroup = new DMFModelGroup(model.name());
        for (SubModel subModel : model.subModels()) {
            var dmfMesh = new DMFMesh();
            var dmfModel = new DMFModel(subModel.name(), dmfMesh);

            List<Mesh> meshes = subModel.meshes();
            for (int i = 0; i < meshes.size(); i++) {
                Mesh mesh = meshes.get(i);
                var vertexBuffers = mesh.vertexBuffers();
                var indexBuffer = mesh.faceBuffer();

                var positionsBuffer = vertexBuffers.get(Semantic.Position);

                int vertexCount = positionsBuffer.count();
                int indexCount = indexBuffer.count();
                var dmfPrimitive = new DMFPrimitive(i, vertexCount, DMFVertexBufferType.MULTI_BUFFER, 0, vertexCount, indexBuffer.componentType().size(), indexCount, 0, indexCount);

                String indexBufferName = "%s_%s_INDICES".formatted(subModel.name(), materials.get(mesh.materialIndex()).name());
                dmfPrimitive.setIndexBufferView(exportBuffer(scene, indexBufferName, indexBuffer), scene);
                dmfPrimitive.materialId = mesh.materialIndex();
                dmfPrimitive.flipUv = true;

                vertexBuffers.forEach((key, value) -> {
                    var dmfVertexAttribute = new DMFVertexAttribute();
                    dmfVertexAttribute.semantic = convertSemantic(key);
                    dmfVertexAttribute.elementType = convertComponentType(value.componentType(), value.normalized());
                    dmfVertexAttribute.elementCount = value.elementType().size();
                    dmfVertexAttribute.offset = 0;
                    dmfVertexAttribute.stride = value.componentType().size() * value.elementType().size();
                    dmfVertexAttribute.size = dmfVertexAttribute.stride;
                    String vertexBufferName = "%s_%s_%s".formatted(subModel.name(), materials.get(mesh.materialIndex()).name(), dmfVertexAttribute.semantic.name());
                    var bufferView = exportBuffer(scene, vertexBufferName, value);
                    dmfVertexAttribute.bufferViewId = scene.getBufferViewId(bufferView);
                    dmfPrimitive.vertexAttributes.put(dmfVertexAttribute.semantic, dmfVertexAttribute);
                });
                dmfMesh.primitives.add(dmfPrimitive);
            }
            modelGroup.children.add(dmfModel);
        }

        for (Material material : materials) {
            var dmfMaterial = scene.createMaterial(material.name());
            for (TextureReference textureRef : material.textures()) {
                var texture = textureRef.supplier().get();
                var outStream = new ByteArrayOutputStream();
                pngExporter.export(texture, outStream);
                var buffer = scene.createBuffer(textureRef.filename(), new ByteArrayDataProvider(outStream.toByteArray()));
                var dmfTexture = new DMFTexture(textureRef.filename(), scene.getBufferId(buffer));
                int textureId = scene.getTextureId(dmfTexture);
                dmfMaterial.textureIds.put(textureRef.type().name(), textureId);
                switch (textureRef.type()) {
                    case Unknown -> {
                    }
                    case Albedo -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "Color"));
                    }
                    case Emissive -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "Emissive"));
                    }
                    case Height -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "Height"));
                    }
                    case Normal -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "Normal"));
                    }
                    case Smoothness -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "Smoothness"));
                    }
                    case Specular -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "Specular"));
                    }
                    case ORM -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "B", "AmbientOcclusion"));
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "G", "Roughness"));
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "R", "Mask"));
                    }
                    case AmbientOcclusion -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "AmbientOcclusion"));
                    }
                    case DetailMask -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "DetailMask"));
                    }
                    case DetailNormal -> {
                        dmfMaterial.textureDescriptors.add(new DMFTextureDescriptor(textureId, "RGB", "DetailNormal"));
                    }
                }
            }
        }

        scene.models.add(modelGroup);
        GSON.toJson(scene, new OutputStreamWriter(out));
    }

    private DMFVertexAttributeSemantic convertSemantic(Semantic semantic) {
        return switch (semantic) {
            case Semantic.Position ignored -> DMFVertexAttributeSemantic.POSITION;
            case Semantic.Normal ignored -> DMFVertexAttributeSemantic.NORMAL;
            case Semantic.Tangent ignored -> DMFVertexAttributeSemantic.TANGENT;
            case Semantic.TexCoord i -> {
                var index = DMFVertexAttributeSemantic.TEXCOORD_0.ordinal();
                Check.index(i.n(), 6);
                yield DMFVertexAttributeSemantic.values()[index + i.n()];
            }
            case Semantic.Color i -> {
                var index = DMFVertexAttributeSemantic.COLOR_0.ordinal();
                Check.index(i.n(), 6);
                yield DMFVertexAttributeSemantic.values()[index + i.n()];
            }
            case Semantic.Joints i -> {
                var index = DMFVertexAttributeSemantic.JOINTS_0.ordinal();
                Check.index(i.n(), 6);
                yield DMFVertexAttributeSemantic.values()[index + i.n()];
            }
            case Semantic.Weights i -> {
                var index = DMFVertexAttributeSemantic.WEIGHTS_0.ordinal();
                Check.index(i.n(), 6);
                yield DMFVertexAttributeSemantic.values()[index + i.n()];
            }
        };
    }

    private String convertComponentType(ComponentType componentType, boolean normalized) {
        return switch (componentType) {
            case Byte ->
                normalized ? DMFComponentType.SIGNED_BYTE_NORMALIZED.name() : DMFComponentType.SIGNED_BYTE.name();
            case UnsignedByte ->
                normalized ? DMFComponentType.UNSIGNED_BYTE_NORMALIZED.name() : DMFComponentType.UNSIGNED_BYTE.name();
            case Short ->
                normalized ? DMFComponentType.SIGNED_SHORT_NORMALIZED.name() : DMFComponentType.SIGNED_SHORT.name();
            case UnsignedShort ->
                normalized ? DMFComponentType.UNSIGNED_SHORT_NORMALIZED.name() : DMFComponentType.UNSIGNED_SHORT.name();
            case UnsignedInt ->
                normalized ? DMFComponentType.UNSIGNED_INT_NORMALIZED.name() : DMFComponentType.UNSIGNED_INT.name();
            case Float -> DMFComponentType.FLOAT.name();
        };
    }


    private DMFBufferView exportBuffer(DMFSceneFile scene, String name, VertexBuffer buffer) {
        byte[] data = switch (buffer.buffer()) {
            case ByteBuffer byteBuffer -> byteBuffer.array();
            case ShortBuffer shortBuffer -> {
                var tmpArray = ByteBuffer.allocate(shortBuffer.limit() * Short.BYTES).order(ByteOrder.LITTLE_ENDIAN);
                for (short i : shortBuffer.array()) {
                    tmpArray.putShort(i);
                }
                yield tmpArray.array();
            }
            case IntBuffer intBuffer -> {
                var tmpArray = ByteBuffer.allocate(intBuffer.limit() * Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN);
                for (int i : intBuffer.array()) {
                    tmpArray.putInt(i);
                }
                yield tmpArray.array();
            }
            case FloatBuffer floatBuffer -> {
                var tmpArray = ByteBuffer.allocate(floatBuffer.limit() * Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);
                for (float i : floatBuffer.array()) {
                    tmpArray.putFloat(i);
                }
                yield tmpArray.array();
            }
            default -> throw new IllegalStateException("Unexpected value: " + buffer.buffer());
        };
        var dmfBuffer = scene.createBuffer(name, new ByteArrayDataProvider(data));
        return scene.createBufferView(scene.getBufferId(dmfBuffer), 0, data.length);
    }

    record ByteArrayDataProvider(byte[] data) implements DMFBuffer.DataProvider {
        @Override
        public InputStream openInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        @Override
        public int length() {
            return data.length;
        }
    }


    private static class JsonTransformSerializer implements JsonSerializer<DMFTransform> {
        @Override
        public JsonElement serialize(DMFTransform src, Type type, JsonSerializationContext context) {
            if (DMFTransform.IDENTITY.equals(src)) {
                return null;
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("position", context.serialize(src.position));
            jsonObject.add("scale", context.serialize(src.scale));
            jsonObject.add("rotation", context.serialize(src.rotation));
            return jsonObject;
        }
    }

    private static class JsonBufferSerializer implements JsonSerializer<DMFBuffer> {
        @Override
        public JsonElement serialize(DMFBuffer src, Type type, JsonSerializationContext context) {
            try {
                return src.serialize(context);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
