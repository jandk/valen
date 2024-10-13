package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.lang.invoke.*;
import java.nio.*;
import java.util.*;

final class ByteArrayDataSource extends DataSource {
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

    ByteArrayDataSource(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    ByteArrayDataSource(byte[] bytes, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, bytes.length);
        this.bytes = bytes;
        this.offset = offset;
        this.pos = offset;
        this.lim = offset + length;
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
        int intPos = Math.toIntExact(pos);
        Check.index(intPos, lim - offset + 1);
        this.pos = this.offset + intPos;
    }

    @Override
    public long size() {
        return lim - offset;
    }

    @Override
    public void close() {
        // Do nothing
    }

    @Override
    public short readShort() {
        var value = (short) ShortVarHandle.get(bytes, pos);
        pos += Short.BYTES;
        return value;
    }

    @Override
    public int readInt() {
        var value = (int) IntVarHandle.get(bytes, pos);
        pos += Integer.BYTES;
        return value;
    }

    @Override
    public long readLong() {
        var value = (long) LongVarHandle.get(bytes, pos);
        pos += Long.BYTES;
        return value;
    }

    @Override
    public float readFloat() {
        var value = (float) FloatVarHandle.get(bytes, pos);
        pos += Float.BYTES;
        return value;
    }

    @Override
    public double readDouble() {
        var value = (double) DoubleVarHandle.get(bytes, pos);
        pos += Double.BYTES;
        return value;
    }
}
