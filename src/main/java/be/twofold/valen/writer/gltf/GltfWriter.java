package be.twofold.valen.writer.gltf;

import be.twofold.valen.geometry.*;
import be.twofold.valen.reader.md6skl.*;
import be.twofold.valen.writer.gltf.model.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public final class GltfWriter {
    private static final ObjectMapper Mapper = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

    private final WritableByteChannel channel;
    private final List<Mesh> sourceMeshes;
    private final Md6Skeleton skeleton;

    private final List<AccessorSchema> accessors = new ArrayList<>();
    private final List<BufferViewSchema> bufferViews = new ArrayList<>();
    private final List<BufferSchema> buffers = new ArrayList<>();
    private final List<MeshSchema> meshes = new ArrayList<>();
    private final List<NodeSchema> nodes = new ArrayList<>();
    private final List<SceneSchema> scenes = new ArrayList<>();
    private final List<SkinSchema> skins = new ArrayList<>();
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
            scenes.get(0).nodes().add(skeletonNode);
        }
        buildBuffers(); // Only now we know the buffer offset

        try {
            String json = Mapper.writeValueAsString(buildGltf());
            byte[] rawJson = json.getBytes(StandardCharsets.US_ASCII);
            int alignedJsonLength = alignedLength(rawJson.length);

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

    private GltfSchema buildGltf() {
        AssetSchema asset = new AssetSchema("valen", "2.0");

        return new GltfSchema(
            asset,
            accessors,
            bufferViews,
            buffers,
            meshes,
            nodes,
            scenes,
            skeleton != null ? skins : null,
            0
        );
    }


    private void buildBuffers() {
        BufferSchema buffer = new BufferSchema(bufferOffset);
        buffers.add(buffer);
    }

    public void buildMeshes() {
        // First we do the meshes
        List<PrimitiveSchema> primitives = sourceMeshes.stream()
            .map(this::buildMeshPrimitive)
            .toList();

        MeshSchema mesh = new MeshSchema(primitives);
        meshes.add(mesh);
    }

    private PrimitiveSchema buildMeshPrimitive(Mesh mesh) {
        ObjectNode attributes = Mapper.getNodeFactory().objectNode();
        Bounds bounds = calculateBounds(mesh.positions());
        attributes.put("POSITION", buildMeshAccessor(mesh.positions().capacity(), BufferType.Position, bounds));
        attributes.put("NORMAL", buildMeshAccessor(mesh.normals().capacity(), BufferType.Normal, null));
        attributes.put("TANGENT", buildMeshAccessor(mesh.tangents().capacity(), BufferType.Tangent, null));
        attributes.put("TEXCOORD_0", buildMeshAccessor(mesh.texCoords().capacity(), BufferType.TexCoordN, null));

        if (skeleton != null) {
            fixJointsWithEmptyWeights(mesh.colors(), mesh.weights());
            attributes.put("JOINTS_0", buildMeshAccessor(mesh.colors().capacity(), BufferType.JointsN, null));
            attributes.put("WEIGHTS_0", buildMeshAccessor(mesh.weights().capacity(), BufferType.WeightsN, null));
        } else {
            attributes.put("COLOR_0", buildMeshAccessor(mesh.colors().capacity(), BufferType.ColorN, null));
        }

        return new PrimitiveSchema(
            attributes,
            buildMeshAccessor(mesh.indices().capacity(), BufferType.Indices, null)
        );
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
        int dataTypeSize = type.getDataType().getSize();
        assert count % dataTypeSize == 0 : "Count must be a multiple of " + dataTypeSize;

        AccessorSchema accessor = new AccessorSchema(
            bufferView,
            type.getComponentType(),
            count / dataTypeSize,
            type.getDataType(),
            bounds != null ? bounds.min() : null,
            bounds != null ? bounds.max() : null,
            type.isNormalized() ? true : null
        );
        accessors.add(accessor);
        return accessors.size() - 1;
    }

    private int buildMeshBufferView(int length, BufferType bufferType) {
        int byteLength = length * bufferType.getComponentType().getSize();

        BufferViewTarget target = switch (bufferType) {
            case InverseBind -> null;
            case Indices -> BufferViewTarget.ELEMENT_ARRAY_BUFFER;
            default -> BufferViewTarget.ARRAY_BUFFER;
        };

        BufferViewSchema bufferView = new BufferViewSchema(
            0,
            bufferOffset,
            byteLength,
            target
        );

        bufferViews.add(bufferView);

        // Round up offset to a multiple of 4
        bufferOffset = alignedLength(bufferOffset + byteLength);
        return bufferViews.size() - 1;
    }

    private void buildNodes() {
        Integer skin = skeleton != null ? 0 : null;
        NodeSchema node = NodeSchema.buildMeshSkin(0, skin);
        nodes.add(node);
    }

    private void buildScenes() {
        List<Integer> nodes = new ArrayList<>();
        nodes.add(0);

        SceneSchema scene = new SceneSchema(nodes);
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
        NodeSchema node = NodeSchema.buildSkeletonNode(
            joint.name(),
            joint.rotation(),
            joint.translation(),
            joint.scale(),
            children.isEmpty() ? null : children
        );
        nodes.add(node);
    }

    private void buildSkeletonSkin(List<Integer> jointIndices) {
        int inverseBindMatrices = buildMeshAccessor(jointIndices.size() * 16, BufferType.InverseBind, null);
        skins.add(new SkinSchema(
            skeletonNode,
            jointIndices,
            inverseBindMatrices
        ));
    }


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

}
