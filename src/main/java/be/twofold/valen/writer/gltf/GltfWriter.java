package be.twofold.valen.writer.gltf;

import be.twofold.valen.geometry.*;
import be.twofold.valen.reader.md6skl.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public final class GltfWriter {
    private final WritableByteChannel channel;
    private final List<Mesh> sourceMeshes;
    private final Md6Skeleton skeleton;

    private final List<JsonObject> accessors = new ArrayList<>();
    private final List<JsonObject> bufferViews = new ArrayList<>();
    private final List<JsonObject> buffers = new ArrayList<>();
    private final List<JsonObject> meshes = new ArrayList<>();
    private final List<JsonObject> nodes = new ArrayList<>();
    private final List<JsonObject> scenes = new ArrayList<>();
    private final List<JsonObject> skins = new ArrayList<>();
    private int bufferOffset;
    private int skeletonNode;

    public GltfWriter(WritableByteChannel channel, List<Mesh> sourceMeshes, Md6Skeleton skeleton) {
        this.channel = channel;
        this.sourceMeshes = sourceMeshes;
        this.skeleton = skeleton;
    }

    public void write() {
        buildMeshes();
        buildNodes();
        buildScenes();
        if (skeleton != null) {
            buildSkeleton();
            // Ugly little fixup
            scenes.get(0).get("nodes").getAsJsonArray().add(skeletonNode);
        }
        buildBuffers(); // Only now we know the buffer offset

        String json = buildGltf().toString();
        byte[] rawJson = json.getBytes(StandardCharsets.US_ASCII);
        int alignedJsonLength = alignedLength(rawJson.length);

        try {
            int totalSize = 12 + 8 + alignedJsonLength + 8 + bufferOffset;
            channel.write(GlbHeader.of(totalSize).toBuffer());
            channel.write(GlbChunkHeader.of(GlbChunkType.Json, alignedJsonLength).toBuffer());
            channel.write(ByteBuffer.wrap(rawJson));
            align(rawJson.length, (byte) ' ');
            channel.write(GlbChunkHeader.of(GlbChunkType.Bin, bufferOffset).toBuffer());
            writeBuffers();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private JsonObject buildGltf() {
        JsonObject object = new JsonObject();
        object.add("asset", buildAsset("valen", "2.0"));
        object.add("accessors", buildArray(accessors));
        object.add("bufferViews", buildArray(bufferViews));
        object.add("buffers", buildArray(buffers));
        object.add("meshes", buildArray(meshes));
        object.add("nodes", buildArray(nodes));
        object.add("scenes", buildArray(scenes));
        if (skeleton != null) {
            object.add("skins", buildArray(skins));
        }
        object.addProperty("scene", 0);
        return object;
    }

    // region Done

    private JsonObject buildAsset(String generator, String version) {
        JsonObject object = new JsonObject();
        object.addProperty("generator", generator);
        object.addProperty("version", version);
        return object;
    }

    private void buildBuffers() {
        JsonObject buffer = new JsonObject();
        buffer.addProperty("byteLength", bufferOffset);
        buffers.add(buffer);
    }

    public void buildMeshes() {
        // First we do the meshes
        List<JsonObject> primitives = sourceMeshes.stream()
            .map(this::buildMeshPrimitive)
            .toList();

        JsonObject mesh = new JsonObject();
        mesh.add("primitives", buildArray(primitives));
        meshes.add(mesh);
    }

    private JsonObject buildMeshPrimitive(Mesh mesh) {
        JsonObject attributes = new JsonObject();
        Bounds bounds = calculateBounds(mesh.positions());
        attributes.addProperty("POSITION", buildMeshAccessor(mesh.positions().capacity(), BufferType.Position, bounds));
        attributes.addProperty("NORMAL", buildMeshAccessor(mesh.normals().capacity(), BufferType.Normal, null));
        attributes.addProperty("TANGENT", buildMeshAccessor(mesh.tangents().capacity(), BufferType.Tangent, null));
        attributes.addProperty("TEXCOORD_0", buildMeshAccessor(mesh.texCoords().capacity(), BufferType.TexCoordN, null));

        if (skeleton != null) {
            fixJointsWithEmptyWeights(mesh.colors(), mesh.weights());
            attributes.addProperty("JOINTS_0", buildMeshAccessor(mesh.colors().capacity(), BufferType.JointsN, null));
            attributes.addProperty("WEIGHTS_0", buildMeshAccessor(mesh.weights().capacity(), BufferType.WeightsN, null));
        } else {
            attributes.addProperty("COLOR_0", buildMeshAccessor(mesh.colors().capacity(), BufferType.ColorN, null));
        }

        JsonObject primitive = new JsonObject();
        primitive.add("attributes", attributes);
        primitive.addProperty("indices", buildMeshAccessor(mesh.indices().capacity(), BufferType.Indices, null));
        primitive.addProperty("mode", 4);
        return primitive;
    }

    private void fixJointsWithEmptyWeights(ByteBuffer joints, ByteBuffer weights) {
        byte[] ja = joints.array();
        byte[] wa = weights.array();
        for (int i = 0; i < ja.length; i++) {
            if (wa[i] == 0) {
                ja[i] = 0;
            }
        }
    }

    private int buildMeshAccessor(int capacity, BufferType bufferType, Bounds bounds) {
        int bufferView = buildMeshBufferView(capacity, bufferType);
        return buildMeshAccessor(bufferView, capacity, bufferType, bounds);
    }

    private int buildMeshAccessor(int bufferView, int count, BufferType type, Bounds bounds) {
        assert count % type.typeSize == 0 : "Count must be a multiple of " + type.typeSize;

        JsonObject object = new JsonObject();
        object.addProperty("bufferView", bufferView);
        object.addProperty("componentType", type.componentType);
        object.addProperty("count", count / type.typeSize);
        object.addProperty("type", type.type);
        if (type.normalized) {
            object.addProperty("normalized", true);
        }
        if (bounds != null) {
            object.add("min", map(bounds.min()));
            object.add("max", map(bounds.max()));
        }
        accessors.add(object);
        return accessors.size() - 1;
    }

    private int buildMeshBufferView(int length, BufferType bufferType) {
        int byteLength = length * bufferType.componentSize;

        JsonObject object = new JsonObject();
        object.addProperty("buffer", 0);
        object.addProperty("byteOffset", bufferOffset);
        object.addProperty("byteLength", byteLength);
        if (bufferType != BufferType.InverseBind) {
            object.addProperty("target", bufferType == BufferType.Indices ? 34963 : 34962);
        }
        bufferViews.add(object);

        // Round up offset to a multiple of 4
        bufferOffset = alignedLength(bufferOffset + byteLength);
        return bufferViews.size() - 1;
    }

    private void buildNodes() {
        JsonObject node = new JsonObject();
        node.addProperty("mesh", 0);
        if (skeleton != null) {
            node.addProperty("skin", 0);
        }
        nodes.add(node);
    }

    private void buildScenes() {
        JsonArray nodes = new JsonArray();
        nodes.add(0);

        JsonObject scene = new JsonObject();
        scene.add("nodes", nodes);

        scenes.add(scene);
    }

    private void buildSkeleton() {
        int offset = nodes.size();
        List<Md6SkeletonJoint> joints = skeleton.joints();

        // Calculate the parent-child relationships
        Map<Integer, List<Integer>> children = new HashMap<>();
        for (int i = 0; i < joints.size(); i++) {
            Md6SkeletonJoint joint = joints.get(i);
            children
                .computeIfAbsent(joint.parent(), __ -> new ArrayList<>())
                .add(offset + i);
        }

        // Build the skeleton
        List<Integer> jointIndices = new ArrayList<>();
        for (int i = 0; i < joints.size(); i++) {
            if (joints.get(i).parent() == -1) {
                skeletonNode = offset + i;
            }
            jointIndices.add(offset + i);
            buildSkeletonJoint(joints.get(i), children.getOrDefault(i, List.of()));
        }

        // Build the skin
        buildSkeletonSkin(jointIndices);
    }

    private void buildSkeletonJoint(Md6SkeletonJoint joint, List<Integer> children) {
        JsonArray childrenArray = new JsonArray();
        for (int child : children) {
            childrenArray.add(child);
        }

        JsonObject node = new JsonObject();
        node.addProperty("name", joint.name());
        node.add("rotation", map(joint.rotation()));
        node.add("translation", map(joint.translation()));
        node.add("scale", map(joint.scale()));
        if (!childrenArray.isEmpty()) {
            node.add("children", childrenArray);
        }
        nodes.add(node);
    }

    private void buildSkeletonSkin(List<Integer> jointIndices) {
        JsonArray joints = new JsonArray();
        for (int jointIndex : jointIndices) {
            joints.add(jointIndex);
        }

        int inverseBindMatrices = buildMeshAccessor(joints.size() * 16, BufferType.InverseBind, null);

        JsonObject skin = new JsonObject();
        skin.addProperty("skeleton", skeletonNode);
        skin.add("joints", joints);
        skin.addProperty("inverseBindMatrices", inverseBindMatrices);
        skins.add(skin);
    }

    // endregion

    // region Helpers

    private int alignedLength(int length) {
        return (length + 3) & ~3;
    }

    private void align(int length, byte pad) throws IOException {
        byte[] padding = new byte[alignedLength(length) - length];
        Arrays.fill(padding, pad);
        channel.write(ByteBuffer.wrap(padding));
    }

    private void writeBuffers() throws IOException {
        for (Mesh mesh : sourceMeshes) {
            writeBuffer(mesh.positions());
            writeBuffer(mesh.normals());
            writeBuffer(mesh.tangents());
            writeBuffer(mesh.texCoords());
            if (skeleton != null) {
                writeBuffer(mesh.colors());
                writeBuffer(mesh.weights());
            } else {
                writeBuffer(mesh.colors());
            }
            writeBuffer(mesh.indices());
        }

        // Write inverse bind matrices
        if (skeleton != null) {
            List<Md6SkeletonJoint> joints = skeleton.joints();
            FloatBuffer buffer = FloatBuffer.allocate(joints.size() * 16);
            for (Md6SkeletonJoint joint : joints) {
                buffer.put(joint.inverseBasePose().transpose().toArray());
            }
            writeBuffer(buffer.flip());
        }
    }

    private void writeBuffer(FloatBuffer floatBuffer) throws IOException {
        ByteBuffer buffer = ByteBuffer
            .allocate(floatBuffer.capacity() * 4)
            .order(ByteOrder.LITTLE_ENDIAN);

        buffer.asFloatBuffer().put(floatBuffer);

        writeBuffer(buffer);
    }

    private void writeBuffer(ShortBuffer shortBuffer) throws IOException {
        ByteBuffer buffer = ByteBuffer
            .allocate(shortBuffer.capacity() * 2)
            .order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < shortBuffer.capacity(); i += 3) {
            buffer.putShort(shortBuffer.get(i));
            buffer.putShort(shortBuffer.get(i + 2));
            buffer.putShort(shortBuffer.get(i + 1));
        }

        writeBuffer(buffer.flip());
    }

    private void writeBuffer(ByteBuffer buffer) throws IOException {
        channel.write(buffer);
        align(buffer.capacity(), (byte) 0);
    }

    private static JsonArray buildArray(List<JsonObject> objects) {
        JsonArray array = new JsonArray();
        objects.forEach(array::add);
        return array;
    }

    private static Bounds calculateBounds(FloatBuffer vertices) {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < vertices.capacity(); i += 3) {
            float x = vertices.get(i);
            float y = vertices.get(i + 1);
            float z = vertices.get(i + 2);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
        return new Bounds(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
    }

    private JsonArray map(Vector3 v) {
        JsonArray array = new JsonArray();
        array.add(v.x());
        array.add(v.y());
        array.add(v.z());
        return array;
    }

    private JsonArray map(Vector4 v) {
        JsonArray array = new JsonArray();
        array.add(v.x());
        array.add(v.y());
        array.add(v.z());
        array.add(v.w());
        return array;
    }

// endregion

    private enum BufferType {
        Position(5126, "VEC3", 4, 3, false),
        Normal(5126, "VEC3", 4, 3, false),
        Tangent(5126, "VEC4", 4, 4, false),
        TexCoordN(5126, "VEC2", 4, 2, false),
        ColorN(5121, "VEC4", 1, 4, true),
        JointsN(5121, "VEC4", 1, 4, false),
        WeightsN(5121, "VEC4", 1, 4, true),
        Indices(5123, "SCALAR", 2, 1, false),
        InverseBind(5126, "MAT4", 4, 16, false);

        private final int componentType;
        private final String type;
        private final int componentSize;
        private final int typeSize;
        private final boolean normalized;

        BufferType(int componentType, String type, int componentSize, int typeSize, boolean normalized) {
            this.componentType = componentType;
            this.type = type;
            this.componentSize = componentSize;
            this.typeSize = typeSize;
            this.normalized = normalized;
        }
    }
}
