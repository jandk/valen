package be.twofold.valen.gltf;

import java.io.*;
import java.nio.*;

interface BufferManager {
    static BufferManager create() {
        return new CachingBufferManager();
    }

    int totalLength();

    GltfContext.OffsetLength addBuffer(Buffer buffer) throws IOException;

    GltfContext.OffsetLength addImageBuffer(ByteBuffer buffer, String filename) throws IOException;

    void write(OutputStream out) throws IOException;

}
