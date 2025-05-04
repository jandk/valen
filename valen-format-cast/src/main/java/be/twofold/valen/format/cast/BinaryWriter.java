package be.twofold.valen.format.cast;

import java.io.*;
import java.lang.invoke.*;
import java.nio.*;
import java.util.*;

final class BinaryWriter implements Closeable {
    private static final VarHandle VH_SHORT = MethodHandles
        .byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN)
        .withInvokeExactBehavior();
    private static final VarHandle VH_INT = MethodHandles
        .byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN)
        .withInvokeExactBehavior();
    private static final VarHandle VH_LONG = MethodHandles
        .byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN)
        .withInvokeExactBehavior();
    private static final VarHandle VH_FLOAT = MethodHandles
        .byteArrayViewVarHandle(float[].class, ByteOrder.LITTLE_ENDIAN)
        .withInvokeExactBehavior();
    private static final VarHandle VH_DOUBLE = MethodHandles
        .byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN)
        .withInvokeExactBehavior();

    private final byte[] buffer = new byte[8];
    private final OutputStream out;

    BinaryWriter(OutputStream out) {
        this.out = Objects.requireNonNull(out);
    }

    public void writeByte(byte b) throws IOException {
        out.write(b);
    }

    public void writeShort(short value) throws IOException {
        VH_SHORT.set(buffer, 0, value);
        out.write(buffer, 0, Short.BYTES);
    }

    public void writeInt(int value) throws IOException {
        VH_INT.set(buffer, 0, value);
        out.write(buffer, 0, Integer.BYTES);
    }

    public void writeLong(long value) throws IOException {
        VH_LONG.set(buffer, 0, value);
        out.write(buffer, 0, Long.BYTES);
    }

    public void writeFloat(float value) throws IOException {
        VH_FLOAT.set(buffer, 0, value);
        out.write(buffer, 0, Float.BYTES);
    }

    public void writeDouble(double value) throws IOException {
        VH_DOUBLE.set(buffer, 0, value);
        out.write(buffer, 0, Double.BYTES);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        out.write(bytes);
    }

    @Override
    public void close() throws IOException {
        out.flush();
    }
}
