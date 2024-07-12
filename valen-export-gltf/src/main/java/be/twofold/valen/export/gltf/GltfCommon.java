package be.twofold.valen.export.gltf;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.gltf.gson.*;
import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

abstract class GltfCommon {
    static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(AbstractId.class, new AbstractIdTypeAdapter().nullSafe())
        .registerTypeHierarchyAdapter(Collection.class, new CollectionSerializer())
        .registerTypeHierarchyAdapter(Map.class, new MapSerializer())
        .registerTypeAdapter(AccessorComponentType.class, new AccessorComponentTypeTypeAdapter())
        .registerTypeAdapter(AccessorType.class, new AccessorTypeTypeAdapter())
        .registerTypeAdapter(AnimationChannelTargetPath.class, new AnimationChannelTargetPathTypeAdapter())
        .registerTypeAdapter(AnimationSamplerInterpolation.class, new AnimationSamplerInterpolationTypeAdapter())
        .registerTypeAdapter(BufferViewTarget.class, new BufferViewTargetTypeAdapter().nullSafe())
        .registerTypeAdapter(MimeType.class, new MimeTypeTypeAdapter().nullSafe())
        .registerTypeAdapter(Matrix4.class, new Matrix4TypeAdapter().nullSafe())
        .registerTypeAdapter(Quaternion.class, new QuaternionTypeAdapter().nullSafe())
        .registerTypeAdapter(Vector2.class, new Vector2TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector3.class, new Vector3TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector4.class, new Vector4TypeAdapter().nullSafe())
        .create();

    final GltfContext context;

    GltfCommon(GltfContext context) {
        this.context = context;
    }

    int alignedLength(int length) {
        return (length + 3) & ~3;
    }

    void align(WritableByteChannel channel, int length, byte pad) throws IOException {
        byte[] padding = new byte[alignedLength(length) - length];
        Arrays.fill(padding, pad);
        channel.write(ByteBuffer.wrap(padding));
    }

    void writeBuffer(WritableByteChannel channel, Buffer buffer) {
        var byteBuffer = Buffers.toByteBuffer(buffer);
        try {
            channel.write(byteBuffer);
            align(channel, byteBuffer.capacity(), (byte) 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    int size(Buffer buffer) {
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
