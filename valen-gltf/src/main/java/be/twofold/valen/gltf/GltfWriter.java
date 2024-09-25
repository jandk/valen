package be.twofold.valen.gltf;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public final class GltfWriter extends GltfCommon {
    public GltfWriter(GltfContext context) {
        super(context);
    }

    public void write(Path outputDirectory, String filename) throws IOException {
        var binFile = Path.of(filename + ".bin");
        getContext().updateBufferViews(URI.create(binFile.toString()));
        try (var out = Files.newOutputStream(outputDirectory.resolve(binFile))) {
            writeBuffers(out);
        }

        try (var out = Files.newOutputStream(outputDirectory.resolve(filename + ".gltf"))) {
            out.write(toRawJson());
        }
    }

    public void writeWithEmbeddedBuffer(OutputStream out) throws IOException {
        writeBuffersEmbedded();
        out.write(toRawJson());
    }
}
