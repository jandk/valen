package be.twofold.valen.gltf;

import be.twofold.valen.gltf.glb.*;
import be.twofold.valen.gltf.model.buffer.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

public final class GlbWriter extends GltfCommon {
    public GlbWriter(GltfContext context) {
        super(context);
    }

    public void write(OutputStream out) throws IOException {
        int buffersTotalSize = 0;
        var visitedBuffers = new HashSet<BufferID>();
        List<BufferViewSchema> bufferViews = context.getBufferViews();
        for (int i = 0; i < bufferViews.size(); i++) {
            BufferViewSchema bufferView = bufferViews.get(i);
            if (visitedBuffers.contains(bufferView.getBuffer())) {
                continue;
            }
            var writable = context.writables.get(bufferView.getBuffer().getId());
            bufferView = BufferViewSchema.builder().from(bufferView).byteOffset(buffersTotalSize).buffer(BufferID.of(0)).build();
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

        int totalSize = GlbHeader.BYTES + GlbChunkHeader.BYTES + alignedJsonLength + GlbChunkHeader.BYTES + buffersTotalSize;
        out.write(new GlbHeader(totalSize).toBuffer().array());
        out.write(new GlbChunkHeader(alignedJsonLength, GlbChunkType.JSON).toBuffer().array());
        out.write(rawJson);
        align(out, rawJson.length, (byte) ' ');
        out.write(new GlbChunkHeader(buffersTotalSize, GlbChunkType.BIN).toBuffer().array());
        for (Buffer buffer : context.writables) {
            writeBuffer(out, buffer);
        }
    }
}
