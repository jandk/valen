package be.twofold.valen.core.io;

import java.io.*;
import java.lang.invoke.*;
import java.nio.*;
import java.util.*;

public final class ByteArrayDataSource extends DataSource {
    private static final VarHandle ShortVarHandle =
        MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle IntVarHandle =
        MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LongVarHandle =
        MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle FloatVarHandle =
        MethodHandles.byteArrayViewVarHandle(float[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle DoubleVarHandle =
        MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN);

    private final byte[] bytes;
    private final int offset;
    private final int lim;
    private int pos;

    public ByteArrayDataSource(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    public ByteArrayDataSource(byte[] bytes, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, bytes.length);
        this.bytes = bytes;
        this.offset = offset;
        this.pos = offset;
        this.lim = offset + length;
    }

    public static ByteArrayDataSource fromBuffer(ByteBuffer buffer) {
        if (!buffer.hasArray()) {
            throw new IllegalArgumentException("ByteBuffer must be backed by an array");
        }
        return new ByteArrayDataSource(buffer.array(), buffer.arrayOffset(), buffer.limit());
    }

    @Override
    public byte readByte() throws IOException {
        if (pos >= lim) {
            throw new EOFException();
        }
        return bytes[pos++];
    }

    @Override
    public void readBytes(byte[] dst, int off, int len, boolean buffered) throws IOException {
        Objects.checkFromIndexSize(off, len, dst.length);
        if (pos + len > lim) {
            throw new EOFException();
        }
        System.arraycopy(bytes, pos, dst, off, len);
        pos += len;
    }

    @Override
    public long tell() {
        return pos - offset;
    }

    @Override
    public void seek(long pos) {
        Objects.checkIndex(pos, lim - offset + 1);
        this.pos = (int) (this.offset + pos);
    }

    @Override
    public long size() {
        return lim - offset;
    }

    @Override
    public void close() {
        // Do nothing
    }

    public ByteArrayDataSource slice(int offset, int length) {
        Objects.checkFromIndexSize(offset, length, lim - this.offset);
        return new ByteArrayDataSource(bytes, this.offset + offset, length);
    }

    @Override
    public short readShort() throws IOException {
        var value = (short) ShortVarHandle.get(bytes, pos);
        pos += Short.BYTES;
        return value;
    }

    @Override
    public int readInt() throws IOException {
        var value = (int) IntVarHandle.get(bytes, pos);
        pos += Integer.BYTES;
        return value;
    }

    @Override
    public long readLong() throws IOException {
        var value = (long) LongVarHandle.get(bytes, pos);
        pos += Long.BYTES;
        return value;
    }

    @Override
    public float readFloat() throws IOException {
        var value = (float) FloatVarHandle.get(bytes, pos);
        pos += Float.BYTES;
        return value;
    }

    @Override
    public double readDouble() throws IOException {
        var value = (double) DoubleVarHandle.get(bytes, pos);
        pos += Double.BYTES;
        return value;
    }
}
