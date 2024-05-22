package be.twofold.valen.export.gltf;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class GltfCommon {

    protected int alignedLength(int length) {
        return (length + 3) & ~3;
    }

    protected void align(WritableByteChannel channel, int length, byte pad) throws IOException {
        byte[] padding = new byte[alignedLength(length) - length];
        Arrays.fill(padding, pad);
        channel.write(ByteBuffer.wrap(padding));
    }

    protected void writeBuffer(WritableByteChannel channel, Buffer buffer) {
        var byteBuffer = Buffers.toByteBuffer(buffer);
        try {
            channel.write(byteBuffer);
            align(channel, byteBuffer.capacity(), (byte) 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected static int size(Buffer buffer) {
        return switch (buffer) {
            case ByteBuffer byteBuffer -> byteBuffer.capacity();
            case ShortBuffer shortBuffer -> shortBuffer.capacity() * Short.BYTES;
            case IntBuffer intBuffer -> intBuffer.capacity() * Integer.BYTES;
            case LongBuffer longBuffer -> longBuffer.capacity() * Long.BYTES;
            case FloatBuffer floatBuffer -> floatBuffer.capacity() * Float.BYTES;
            case DoubleBuffer doubleBuffer -> doubleBuffer.capacity() * Double.BYTES;
            case CharBuffer charBuffer -> charBuffer.capacity() * Character.BYTES;
        };
    }
}
