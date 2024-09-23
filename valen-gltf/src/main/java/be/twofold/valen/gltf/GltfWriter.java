package be.twofold.valen.gltf;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.buffer.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class GltfWriter extends GltfCommon {
    public GltfWriter(GltfContext context) {
        super(context);
    }

    public void write(Path outputDirectory, String filename) throws IOException {
        try (var channel = Files.newOutputStream(outputDirectory.resolve(filename + ".bin"))) {
            int buffersTotalSize = 0;
            var visitedBuffers = new HashSet<BufferID>();
            List<BufferViewSchema> bufferViews = context.getBufferViews();
            for (int i = 0; i < bufferViews.size(); i++) {
                BufferViewSchema bufferView = bufferViews.get(i);
                if (visitedBuffers.contains(bufferView.getBuffer())) {
                    continue;
                }
                var writable = context.writables.get(bufferView.getBuffer().id());
                bufferView = BufferViewSchema.builder().from(bufferView).byteOffset(buffersTotalSize).buffer(BufferID.of(0)).build();
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

        try (var channel = Files.newOutputStream(outputDirectory.resolve(filename + ".gltf"))) {
            GltfSchema gltf = context.buildGltf();
            String json = GltfCommon.GSON.toJson(gltf);
            channel.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }
}
