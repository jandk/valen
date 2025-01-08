package be.twofold.valen.gltf;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

final class WritingBufferManager implements BufferManager, Closeable {
    private final Path imagePath;
    private final OutputStream out;
    private int totalLength = 0;

    WritingBufferManager(Path binPath, Path imagePath) throws IOException {
        this.out = Files.newOutputStream(binPath);
        this.imagePath = Objects.requireNonNull(imagePath);
    }

    @Override
    public int totalLength() {
        return totalLength;
    }

    @Override
    public GltfContext.OffsetLength addBuffer(Buffer buffer) throws IOException {
        int offset = totalLength;
        int length = Buffers.byteSize(buffer);

        out.write(Buffers.toByteArray(buffer));
        GltfUtils.align(out, length, (byte) 0);
        totalLength = GltfUtils.alignedLength(totalLength + length);
        return new GltfContext.OffsetLength(offset, length);
    }

    @Override
    public GltfContext.OffsetLength addImageBuffer(ByteBuffer buffer, String filename) throws IOException {
        var outPath = imagePath.resolve(filename);
        Files.write(outPath, buffer.array());
        return null;
    }

    @Override
    public void write(OutputStream out) throws IOException {
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
