package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;

final class ByteArrayDataSource implements DataSource, Closeable {
    private final byte[] array;
    private final int offset;
    private final int limit;
    private int position;

    ByteArrayDataSource(byte[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        this.array = array;
        this.offset = offset;
        this.position = offset;
        this.limit = offset + length;
    }

    @Override
    public void read(ByteBuffer dst) {
        int remaining = dst.remaining();
        dst.put(array, position, remaining);
        position += remaining;
    }

    @Override
    public long size() {
        return limit - offset;
    }

    @Override
    public long position() {
        return position - offset;
    }

    @Override
    public void position(long pos) {
        int intPos = Math.toIntExact(pos);
        Check.index(intPos, limit - offset + 1);
        this.position = offset + intPos;
    }

    @Override
    public byte readByte() throws IOException {
        if (position >= limit) {
            throw new EOFException();
        }
        return array[position++];
    }

    @Override
    public short readShort() {
        var value = ByteArrays.getShort(array, position);
        position += Short.BYTES;
        return value;
    }

    @Override
    public int readInt() {
        var value = ByteArrays.getInt(array, position);
        position += Integer.BYTES;
        return value;
    }

    @Override
    public long readLong() {
        var value = ByteArrays.getLong(array, position);
        position += Long.BYTES;
        return value;
    }

    @Override
    public float readFloat() {
        var value = ByteArrays.getFloat(array, position);
        position += Float.BYTES;
        return value;
    }

    @Override
    public double readDouble() {
        var value = ByteArrays.getDouble(array, position);
        position += Double.BYTES;
        return value;
    }
}
