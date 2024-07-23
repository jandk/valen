package be.twofold.valen.gltf;

import be.twofold.valen.gltf.glb.*;
import be.twofold.valen.gltf.model.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public final class GlbWriter extends GltfCommon {
    public GlbWriter(GltfContext context) {
        super(context);
    }

    public void write(WritableByteChannel channel) throws IOException {
        int buffersTotalSize = 0;
        var visitedBuffers = new HashSet<BufferId>();
        List<BufferViewSchema> bufferViews = context.getBufferViews();
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

        BufferSchema bufferSchema = BufferSchema.builder()
            .byteLength(buffersTotalSize)
            .build();
        context.getBuffers().add(bufferSchema);

        String json = GltfCommon.GSON.toJson(context.buildGltf());
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
