package be.twofold.valen.core.geometry.read;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

@FunctionalInterface
public interface AttributeReader<T> {
    void read(BinaryReader reader, T target, int offset) throws IOException;

    static AttributeReader<Floats.Mutable> readVector2(float scale, Vector2 bias) {
        return (reader, target, offset) -> {
            Vector2.read(reader).multiply(scale).add(bias).toSlice(target, offset);
        };
    }

    static AttributeReader<Floats.Mutable> readVector3(float scale, Vector3 bias) {
        return (reader, target, offset) -> {
            Vector3.read(reader).multiply(scale).add(bias).toSlice(target, offset);
        };
    }

    static AttributeReader<Ints.Mutable> readShortAsInts() {
        return (reader, target, offset) -> {
            target.set(offset, reader.readUnsignedShort());
        };
    }

    static AttributeReader<Shorts.Mutable> copyBytesAsShorts(int n) {
        return (reader, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.set(offset + i, (short) reader.readUnsignedByte());
            }
        };
    }

    static AttributeReader<Floats.Mutable> copyBytesAsFloats(int n) {
        return (reader, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.set(offset + i, FloatMath.unpackUNorm8(reader.readByte()));
            }
        };
    }

    static AttributeReader<Bytes.Mutable> copyBytes(int n) {
        return (reader, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.set(offset + i, reader.readByte());
            }
        };
    }
}
