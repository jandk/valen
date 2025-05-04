package be.twofold.valen.format.cast;

import java.io.*;
import java.lang.invoke.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;

final class BinaryReader implements Closeable {
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
    private final InputStream in;

    public BinaryReader(InputStream in) {
        this.in = Objects.requireNonNull(in);
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = in.readNBytes(length);
        if (bytes.length != length) {
            throw new EOFException("Expected " + length + " bytes but got " + bytes.length);
        }
        return bytes;
    }

    public byte readByte() throws IOException {
        int read = in.read();
        if (read < 0) {
            throw new EOFException("Unexpected end of stream");
        }
        return (byte) read;
    }

    public short readShort() throws IOException {
        buffer(Short.BYTES);
        return (short) VH_SHORT.get(buffer, 0);
    }

    public int readInt() throws IOException {
        buffer(Integer.BYTES);
        return (int) VH_INT.get(buffer, 0);
    }

    public long readLong() throws IOException {
        buffer(Long.BYTES);
        return (long) VH_LONG.get(buffer, 0);
    }

    public float readFloat() throws IOException {
        buffer(Float.BYTES);
        return (float) VH_FLOAT.get(buffer, 0);
    }

    public double readDouble() throws IOException {
        buffer(Double.BYTES);
        return (double) VH_DOUBLE.get(buffer, 0);
    }

    public String readCString() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            byte b = readByte();
            if (b == 0) {
                break;
            }
            out.write(b);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    public String readString(int length) throws IOException {
        byte[] bytes = readBytes(length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public ByteBuffer readBuffer(int length) throws IOException {
        byte[] bytes = readBytes(length);
        return ByteBuffer.wrap(bytes)
            .order(ByteOrder.LITTLE_ENDIAN);
    }

    public <T> List<T> readObjects(int count, Function<BinaryReader, T> reader) throws IOException {
        ArrayList<T> result = new ArrayList<T>(count);
        for (int i = 0; i < count; i++) {
            result.add(reader.apply(this));
        }
        return List.copyOf(result);
    }

    private void buffer(int length) throws IOException {
        int read = in.read(buffer, 0, length);
        if (read != length) {
            throw new EOFException("Expected " + length + " bytes but got " + read);
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
