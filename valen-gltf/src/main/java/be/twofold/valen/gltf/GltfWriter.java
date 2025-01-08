package be.twofold.valen.gltf;

import be.twofold.valen.gltf.glb.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class GltfWriter {
    private final GltfContext context;

    public GltfWriter(GltfContext context) {
        this.context = Objects.requireNonNull(context);
    }

    public void write(Path path) throws IOException {
        writeGltfPlusBin(path);
    }

    private void writeGlb(Path path) throws IOException {
        context.finalizeBuffers(null);
        var rawJson = context.toRawJson();
        var jsonSize = GltfUtils.alignedLength(rawJson.length);
        var binSize = context.buffersLength();

        var totalSize = GlbHeader.BYTES + GlbChunkHeader.BYTES + jsonSize + GlbChunkHeader.BYTES + binSize;

        try (var out = Files.newOutputStream(path)) {
            out.write(new GlbHeader(totalSize).toBuffer().array());
            out.write(new GlbChunkHeader(jsonSize, GlbChunkType.JSON).toBuffer().array());
            out.write(rawJson);
            GltfUtils.align(out, rawJson.length, (byte) ' ');
            out.write(new GlbChunkHeader(binSize, GlbChunkType.BIN).toBuffer().array());
            context.writeBuffers(out);
        }
    }

    private void writeGltfPlusBin(Path path) throws IOException {
        var outputDirectory = path.getParent();
        var fileName = path.getFileName().toString();
        var fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

        var binFileName = fileNameWithoutExtension + ".bin";
        try (var out = Files.newOutputStream(outputDirectory.resolve(binFileName))) {
            context.finalizeBuffers(URI.create(binFileName));
            context.writeBuffers(out);
        }

        try (var output = Files.newOutputStream(outputDirectory.resolve(fileNameWithoutExtension + ".gltf"));
             var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            JsonWriter.create().writeJson(context.buildGltf(), writer, true);
        }
    }
}
