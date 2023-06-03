package be.twofold.valen.writer.gltf;

import be.twofold.valen.geometry.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public final class GltfWriter {
    private final WritableByteChannel channel;
    private final List<Mesh> sourceMeshes;

    private final List<JsonObject> accessors = new ArrayList<>();
    private final List<JsonObject> bufferViews = new ArrayList<>();
    private final List<JsonObject> buffers = new ArrayList<>();
    private final List<JsonObject> meshes = new ArrayList<>();
    private final List<JsonObject> nodes = new ArrayList<>();
    private final List<JsonObject> scenes = new ArrayList<>();
    private int bufferLength;

    public GltfWriter(WritableByteChannel channel, List<Mesh> sourceMeshes) {
        this.channel = channel;
        this.sourceMeshes = sourceMeshes;
    }

    public void write() {
        buildAccessors();
        buildBufferViews();
        buildBuffers();
        buildMeshes();
        buildNodes();
        buildScenes();

        String json = buildGltf().toString();
        byte[] rawJson = json.getBytes(StandardCharsets.US_ASCII);
        int alignedJsonLength = alignedLength(rawJson.length);

        try {
            int totalSize = 12 + 8 + alignedJsonLength + 8 + bufferLength;
            channel.write(GlbHeader.of(totalSize).toBuffer());
            channel.write(GlbChunkHeader.of(GlbChunkType.Json, alignedJsonLength).toBuffer());
            channel.write(ByteBuffer.wrap(rawJson));
            align(rawJson.length, (byte) ' ');
            channel.write(GlbChunkHeader.of(GlbChunkType.Bin, bufferLength).toBuffer());
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

    private void buildAccessors() {
        int bufferView = 0;
        for (Mesh mesh : sourceMeshes) {
            Bounds bounds = calculateBounds(mesh.vertices());
            accessors.add(buildAccessor(bufferView++, 5126, mesh.vertices().capacity(), "VEC3", bounds));
            accessors.add(buildAccessor(bufferView++, 5126, mesh.normals().capacity(), "VEC3", null));
            accessors.add(buildAccessor(bufferView++, 5126, mesh.tangents().capacity(), "VEC4", null));
            accessors.add(buildAccessor(bufferView++, 5126, mesh.texCoords().capacity(), "VEC2", null));
            accessors.add(buildAccessor(bufferView++, 5123, mesh.indices().capacity(), "SCALAR", null));
        }
    }

    private JsonObject buildAccessor(int bufferView, int componentType, int count, String type, Bounds bounds) {
        int elementSize = switch (type) {
            case "SCALAR" -> 1;
            case "VEC2" -> 2;
            case "VEC3" -> 3;
            case "VEC4" -> 4;
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };

        assert count % elementSize == 0 : "Count must be a multiple of " + elementSize;

        JsonObject result = new JsonObject();
        result.addProperty("bufferView", bufferView);
        result.addProperty("componentType", componentType);
        result.addProperty("count", count / elementSize);
        result.addProperty("type", type);
        if (bounds != null) {
            result.add("min", map(bounds.min()));
            result.add("max", map(bounds.max()));
        }
        return result;
    }

    private void buildBufferViews() {
        int offset = 0;
        for (Mesh mesh : sourceMeshes) {
            offset = buildBufferView(offset, mesh.vertices().capacity() * 4, false);
            offset = buildBufferView(offset, mesh.normals().capacity() * 4, false);
            offset = buildBufferView(offset, mesh.tangents().capacity() * 4, false);
            offset = buildBufferView(offset, mesh.texCoords().capacity() * 4, false);
            offset = buildBufferView(offset, mesh.indices().capacity() * 2, true);
        }
        bufferLength = offset;
    }

    private int buildBufferView(int offset, int length, boolean indices) {
        JsonObject object = new JsonObject();
        object.addProperty("buffer", 0);
        object.addProperty("byteOffset", offset);
        object.addProperty("byteLength", length);
        object.addProperty("target", indices ? 34963 : 34962);
        bufferViews.add(object);

        // Round up offset to a multiple of 4
        return alignedLength(offset + length);
    }

    private void buildBuffers() {
        buffers.add(buildBuffer());
    }

    private JsonObject buildBuffer() {
        JsonObject object = new JsonObject();
        object.addProperty("byteLength", bufferLength);
        return object;
    }

    private void buildMeshes() {
        JsonArray primitives = buildArray(buildMeshPrimitives());
        JsonObject mesh = new JsonObject();
        mesh.add("primitives", primitives);
        meshes.add(mesh);
    }

    private List<JsonObject> buildMeshPrimitives() {
        AtomicInteger accessor = new AtomicInteger();
        List<JsonObject> primitives = new ArrayList<>();
        for (int i = 0; i < sourceMeshes.size(); i++) {
            primitives.add(buildMeshPrimitive(accessor));
        }
        return primitives;
    }

    private JsonObject buildMeshPrimitive(AtomicInteger accessor) {
        JsonObject attributes = new JsonObject();
        attributes.addProperty("POSITION", accessor.getAndIncrement());
        attributes.addProperty("NORMAL", accessor.getAndIncrement());
        attributes.addProperty("TANGENT", accessor.getAndIncrement());
        attributes.addProperty("TEXCOORD_0", accessor.getAndIncrement());

        JsonObject primitive = new JsonObject();
        primitive.add("attributes", attributes);
        primitive.addProperty("indices", accessor.getAndIncrement());
        primitive.addProperty("mode", 4);
        return primitive;
    }

    private void buildNodes() {
        JsonObject node = new JsonObject();
        node.addProperty("mesh", 0);
        nodes.add(node);
    }

    private void buildScenes() {
        JsonArray nodes = new JsonArray();
        nodes.add(0);

        JsonObject scene = new JsonObject();
        scene.add("nodes", nodes);

        scenes.add(scene);
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
            writeBuffer(mesh.vertices());
            writeBuffer(mesh.normals());
            writeBuffer(mesh.tangents());
            writeBuffer(mesh.texCoords());
            writeBuffer(mesh.indices());
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

}
