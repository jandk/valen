package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

@FunctionalInterface
public interface GeoReader<T> {
    void read(BinarySource source, T target, int offset) throws IOException;

    static GeoReader<Floats.Mutable> readVector2(float scale, Vector2 bias) {
        return (source, target, offset) -> {
            Vector2.read(source).multiply(scale).add(bias).toSlice(target, offset);
        };
    }

    static GeoReader<Floats.Mutable> readVector3(float scale, Vector3 bias) {
        return (source, target, offset) -> {
            Vector3.read(source).multiply(scale).add(bias).toSlice(target, offset);
        };
    }

    static GeoReader<Ints.Mutable> readShortAsInts() {
        return (source, target, offset) -> {
            target.set(offset, Short.toUnsignedInt(source.readShort()));
        };
    }

    static GeoReader<Shorts.Mutable> copyBytesAsShorts(int n) {
        return (source, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.set(offset + i, (short) Byte.toUnsignedInt(source.readByte()));
            }
        };
    }

    static GeoReader<Floats.Mutable> copyBytesAsFloats(int n) {
        return (source, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.set(offset + i, FloatMath.unpackUNorm8(source.readByte()));
            }
        };
    }

    static GeoReader<Bytes.Mutable> copyBytes(int n) {
        return (source, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.set(offset + i, source.readByte());
            }
        };
    }
}
