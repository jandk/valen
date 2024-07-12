package be.twofold.valen.export.gltf;

import be.twofold.valen.export.gltf.model.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class GltfWriter extends GltfCommon {
    public GltfWriter(GltfContext context) {
        super(context);
    }

    public void write(Path outputDirectory, String filename) throws IOException {
        try (var channel = Files.newByteChannel(outputDirectory.resolve(filename + ".bin"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
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

                writeBuffer(channel, writable);
            }

            BufferSchema buffer = BufferSchema.builder()
                .byteLength(buffersTotalSize)
                .uri(URI.create(filename + ".bin"))
                .build();
            context.getBuffers().add(buffer);

        }

        try (var channel = Files.newByteChannel(outputDirectory.resolve(filename + ".gltf"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GltfSchema gltf = context.buildGltf();
            String json = GSON.toJson(gltf);
            channel.write(ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8)));
        }
    }
}
