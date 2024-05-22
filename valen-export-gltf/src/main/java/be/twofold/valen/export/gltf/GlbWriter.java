package be.twofold.valen.export.gltf;

import be.twofold.valen.core.math.*;
import be.twofold.valen.export.gltf.glb.*;
import be.twofold.valen.export.gltf.gson.*;
import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public final class GlbWriter extends GltfCommon {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(AbstractId.class, new AbstractIdTypeAdapter().nullSafe())
        .registerTypeHierarchyAdapter(Collection.class, new CollectionSerializer())
        .registerTypeHierarchyAdapter(Map.class, new MapSerializer())
        .registerTypeAdapter(AccessorComponentType.class, new AccessorComponentTypeTypeAdapter())
        .registerTypeAdapter(AccessorType.class, new AccessorTypeTypeAdapter())
        .registerTypeAdapter(AnimationChannelTargetPath.class, new AnimationChannelTargetPathTypeAdapter())
        .registerTypeAdapter(AnimationSamplerInterpolation.class, new AnimationSamplerInterpolationTypeAdapter())
        .registerTypeAdapter(BufferViewTarget.class, new BufferViewTargetTypeAdapter().nullSafe())
        .registerTypeAdapter(Matrix4.class, new Matrix4TypeAdapter().nullSafe())
        .registerTypeAdapter(Quaternion.class, new QuaternionTypeAdapter().nullSafe())
        .registerTypeAdapter(Vector2.class, new Vector2TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector3.class, new Vector3TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector4.class, new Vector4TypeAdapter().nullSafe())
        .create();

    private final GltfContext context;

    public GlbWriter(GltfContext context) {
        this.context = context;
    }

    public void write(WritableByteChannel channel) throws IOException {
        int buffersTotalSize = 0;
        {
            var visitedBuffers = new HashSet<BufferId>();
            List<BufferViewSchema> bufferViews = context.bufferViews;
            for (int i = 0; i < bufferViews.size(); i++) {
                BufferViewSchema bufferView = bufferViews.get(i);
                if (visitedBuffers.contains(bufferView.getBuffer())) {
                    continue;
                }
                var writable = context.writables.get(bufferView.getBuffer().getId());
                bufferView = BufferViewSchema.builder().from(bufferView).byteOffset(buffersTotalSize).buffer(BufferId.of(0)).build();
                bufferViews.set(i, bufferView);
                buffersTotalSize = alignedLength(buffersTotalSize + size(writable));
                visitedBuffers.add(bufferView.getBuffer());
            }

            BufferSchema buffer = BufferSchema.builder()
                .byteLength(buffersTotalSize)
                .build();
            context.buffers.add(buffer);
        }

        String json = GSON.toJson(context.buildGltf());
        byte[] rawJson = json.getBytes(StandardCharsets.UTF_8);
        int alignedJsonLength = alignedLength(rawJson.length);

        int totalSize = 12 + 8 + alignedJsonLength + 8 + buffersTotalSize;
        channel.write(GlbHeader.of(totalSize).toBuffer());
        channel.write(GlbChunkHeader.of(GlbChunkType.Json, alignedJsonLength).toBuffer());
        channel.write(ByteBuffer.wrap(rawJson));
        align(channel, rawJson.length, (byte) ' ');
        channel.write(GlbChunkHeader.of(GlbChunkType.Bin, buffersTotalSize).toBuffer());
        for (Buffer buffer : context.writables) {
            writeBuffer(channel, buffer);
        }
    }
}
