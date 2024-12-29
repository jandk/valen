package be.twofold.valen.gltf;

import be.twofold.valen.gltf.gson.*;
import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.types.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

abstract class GltfCommon {
    private static final Gson GSON = new GsonBuilder()
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
        .disableHtmlEscaping()
        .create();

    private final GltfContext context;

    GltfCommon(GltfContext context) {
        this.context = context;
    }

    GltfContext getContext() {
        return context;
    }

    byte[] toRawJson() {
        return GSON
            .toJson(context.buildGltf())
            .getBytes(StandardCharsets.UTF_8);
    }

    public void writeBuffers(OutputStream out) throws IOException {
        for (var buffer : context.getBinaryBuffers()) {
            var byteBuffer = toArray(buffer);
            out.write(byteBuffer);
            GltfUtils.align(out, byteBuffer.length, (byte) 0);
        }
    }

    private byte[] toArray(Buffer buffer) {
        buffer.rewind();
        switch (buffer) {
            case ByteBuffer byteBuffer -> {
                return byteBuffer.array();
            }
            case ShortBuffer shortBuffer -> {
                var bb = ByteBuffer
                    .allocate(shortBuffer.limit() * Short.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asShortBuffer().put(shortBuffer);
                return bb.array();
            }
            case IntBuffer intBuffer -> {
                var bb = ByteBuffer
                    .allocate(intBuffer.limit() * Integer.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asIntBuffer().put(intBuffer);
                return bb.array();
            }
            case LongBuffer longBuffer -> {
                var bb = ByteBuffer
                    .allocate(longBuffer.limit() * Long.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asLongBuffer().put(longBuffer);
                return bb.array();
            }
            case FloatBuffer floatBuffer -> {
                var bb = ByteBuffer
                    .allocate(floatBuffer.limit() * Float.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asFloatBuffer().put(floatBuffer);
                return bb.array();
            }
            case DoubleBuffer doubleBuffer -> {
                var bb = ByteBuffer
                    .allocate(doubleBuffer.limit() * Double.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asDoubleBuffer().put(doubleBuffer);
                return bb.array();
            }
            case CharBuffer charBuffer -> {
                var bb = ByteBuffer
                    .allocate(charBuffer.limit() * Character.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN);
                bb.asCharBuffer().put(charBuffer);
                return bb.array();
            }
        }
    }
}
