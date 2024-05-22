package be.twofold.valen.export.gltf;

import be.twofold.valen.core.math.*;
import be.twofold.valen.export.gltf.gson.*;
import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class GltfWriter extends GltfCommon {
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


    public int embedSizeThreshold;

    public GltfWriter(GltfContext context) {
        this(context, 8192);
    }

    public GltfWriter(GltfContext context, int embedSizeThreshold) {
        this.context = context;
        this.embedSizeThreshold = embedSizeThreshold;
    }

    public void write(Path outputDirectory, String filename) throws IOException {

        try (var channel = Files.newByteChannel(outputDirectory.resolve(filename + ".bin"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
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

                    writeBuffer(channel, writable);
                }

                BufferSchema buffer = BufferSchema.builder()
                    .byteLength(buffersTotalSize)
                    .uri(URI.create(filename + ".bin"))
                    .build();
                context.buffers.add(buffer);
            }

        }

        try (var channel = Files.newByteChannel(outputDirectory.resolve(filename + ".gltf"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GltfSchema gltf = context.buildGltf();
            String json = GSON.toJson(gltf);
            channel.write(ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8)));
        }
    }
}
