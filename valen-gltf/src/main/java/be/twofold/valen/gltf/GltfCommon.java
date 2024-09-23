package be.twofold.valen.gltf;

import be.twofold.valen.gltf.gson.*;
import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.types.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.util.*;

abstract class GltfCommon {
    static final Gson GSON = new GsonBuilder()
        // .setPrettyPrinting()
        .registerTypeHierarchyAdapter(GltfID.class, new GltfIDTypeAdapter().nullSafe())
        .registerTypeHierarchyAdapter(ValueEnum.class, new ValueEnumTypeAdapter().nullSafe())
        .registerTypeHierarchyAdapter(Collection.class, new CollectionSerializer())
        .registerTypeHierarchyAdapter(Map.class, new MapSerializer())
        .registerTypeAdapter(Mat2.class, new Mat2TypeAdapter().nullSafe())
        .registerTypeAdapter(Mat3.class, new Mat3TypeAdapter().nullSafe())
        .registerTypeAdapter(Mat4.class, new Mat4TypeAdapter().nullSafe())
        .registerTypeAdapter(Vec2.class, new Vec2TypeAdapter().nullSafe())
        .registerTypeAdapter(Vec3.class, new Vec3TypeAdapter().nullSafe())
        .registerTypeAdapter(Vec4.class, new Vec4TypeAdapter().nullSafe())
        .create();

    final GltfContext context;

    GltfCommon(GltfContext context) {
        this.context = context;
    }

    int alignedLength(int length) {
        return (length + 3) & ~3;
    }

    void align(OutputStream output, int length, byte pad) throws IOException {
        byte[] padding = new byte[alignedLength(length) - length];
        Arrays.fill(padding, pad);
        output.write(padding);
    }

    void writeBuffer(OutputStream output, Buffer buffer) {
        var byteBuffer = toArray(buffer);
        try {
            output.write(byteBuffer);
            align(output, byteBuffer.length, (byte) 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    int size(Buffer buffer) {
        return switch (buffer) {
            case ByteBuffer bb -> bb.capacity();
            case ShortBuffer sb -> sb.capacity() * Short.BYTES;
            case IntBuffer ib -> ib.capacity() * Integer.BYTES;
            case LongBuffer lb -> lb.capacity() * Long.BYTES;
            case FloatBuffer fb -> fb.capacity() * Float.BYTES;
            case DoubleBuffer db -> db.capacity() * Double.BYTES;
            case CharBuffer cb -> cb.capacity() * Character.BYTES;
        };
    }

    byte[] toArray(Buffer buffer) {
        buffer.rewind();
        switch (buffer) {
            case ByteBuffer byteBuffer -> {
                return byteBuffer.array();
            }
            case ShortBuffer shortBuffer -> {
                var bb = ByteBuffer
                    .allocate(shortBuffer.capacity() * Short.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asShortBuffer().put(shortBuffer);
                return bb.array();
            }
            case IntBuffer intBuffer -> {
                var bb = ByteBuffer
                    .allocate(intBuffer.capacity() * Integer.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asIntBuffer().put(intBuffer);
                return bb.array();
            }
            case LongBuffer longBuffer -> {
                var bb = ByteBuffer
                    .allocate(longBuffer.capacity() * Long.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asLongBuffer().put(longBuffer);
                return bb.array();
            }
            case FloatBuffer floatBuffer -> {
                var bb = ByteBuffer
                    .allocate(floatBuffer.capacity() * Float.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asFloatBuffer().put(floatBuffer);
                return bb.array();
            }
            case DoubleBuffer doubleBuffer -> {
                var bb = ByteBuffer
                    .allocate(doubleBuffer.capacity() * Double.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asDoubleBuffer().put(doubleBuffer);
                return bb.array();
            }
            case CharBuffer charBuffer -> {
                var bb = ByteBuffer
                    .allocate(charBuffer.capacity() * Character.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asCharBuffer().put(charBuffer);
                return bb.array();
            }
        }
    }
}
