package be.twofold.valen.gltf;

import be.twofold.valen.gltf.glb.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;

public final class GltfWriter extends GltfContext implements Closeable {
    private GltfWriter(OutputStream binOutput, Path imagePath) {
        super(binOutput, imagePath);
    }

    public static GltfWriter createGlbWriter() {
        return new GltfWriter(null, null);
    }

    public static GltfWriter createSplitWriter(Path binPath, Path imagePath) throws IOException {
        var output = Files.newOutputStream(binPath);
        return new GltfWriter(output, imagePath);
    }

    public void write(Path path) throws IOException {
        writeGlb(path);
    }

    private void writeGlb(Path path) throws IOException {
        finalizeBuffers(null);
        var rawJson = toRawJson();
        var jsonSize = GltfUtils.alignedLength(rawJson.length);
        var binSize = buffersLength();

        var totalSize = GlbHeader.BYTES + GlbChunkHeader.BYTES + jsonSize + GlbChunkHeader.BYTES + binSize;

        try (var out = Files.newOutputStream(path)) {
            out.write(new GlbHeader(totalSize).toBuffer().array());
            out.write(new GlbChunkHeader(jsonSize, GlbChunkType.JSON).toBuffer().array());
            out.write(rawJson);
            GltfUtils.align(out, rawJson.length, (byte) ' ');
            out.write(new GlbChunkHeader(binSize, GlbChunkType.BIN).toBuffer().array());
            writeBuffers(out);
        }
    }

    private void writeGltfPlusBin(Path path) throws IOException {
        var outputDirectory = path.getParent();
        var fileName = path.getFileName().toString();
        var fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

        var binFileName = fileNameWithoutExtension + ".bin";
        var imagePath = outputDirectory.resolve("_images");
        Files.createDirectories(imagePath);
        try (var out = Files.newOutputStream(outputDirectory.resolve(binFileName))) {
            finalizeBuffers(URI.create(binFileName));
            writeBuffers(out);
        }

        try (var output = Files.newOutputStream(outputDirectory.resolve(fileNameWithoutExtension + ".gltf"));
             var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            JsonWriter.create().writeJson(buildGltf(), writer, true);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
