package be.twofold.valen.gltf;

import java.io.*;
import java.nio.*;
import java.util.*;

final class CachingBufferManager implements BufferManager {
    private final List<Buffer> buffers = new ArrayList<>();
    private int totalLength = 0;

    @Override
    public int totalLength() {
        return totalLength;
    }

    @Override
    public GltfContext.OffsetLength addBuffer(Buffer buffer) {
        int offset = totalLength;
        int length = Buffers.byteSize(buffer);

        buffers.add(buffer);
        totalLength = GltfUtils.alignedLength(totalLength + length);
        return new GltfContext.OffsetLength(offset, length);
    }

    @Override
    public GltfContext.OffsetLength addImageBuffer(ByteBuffer buffer, String filename) {
        return addBuffer(buffer);
    }

    @Override
    public void write(OutputStream out) throws IOException {
        for (var buffer : buffers) {
            var array = Buffers.toByteArray(buffer);
            out.write(array);
            GltfUtils.align(out, array.length, (byte) 0);
        }
    }
}
