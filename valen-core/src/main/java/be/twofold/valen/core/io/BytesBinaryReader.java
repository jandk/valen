package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.nio.*;

final class BytesBinaryReader implements BinaryReader {
    private final Bytes bytes;
    private int position = 0;

    BytesBinaryReader(Bytes bytes) {
        this.bytes = bytes;
    }

    @Override
    public void read(ByteBuffer dst) {
        int length = dst.remaining();
        Buffers.copy(bytes.slice(position).asBuffer(), dst);
        position += length;
    }

    @Override
    public long size() {
        return bytes.length();
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public BinaryReader position(long pos) {
        position = Math.toIntExact(pos);
        return this;
    }

    @Override
    public byte readByte() {
        byte result = bytes.getByte(position);
        position += Byte.BYTES;
        return result;
    }

    @Override
    public short readShort() {
        short result = bytes.getShort(position);
        position += Short.BYTES;
        return result;
    }

    @Override
    public int readInt() {
        int result = bytes.getInt(position);
        position += Integer.BYTES;
        return result;
    }

    @Override
    public long readLong() {
        long result = bytes.getLong(position);
        position += Long.BYTES;
        return result;
    }

    @Override
    public float readFloat() {
        float result = bytes.getFloat(position);
        position += Float.BYTES;
        return result;
    }

    @Override
    public double readDouble() {
        double result = bytes.getDouble(position);
        position += Double.BYTES;
        return result;
    }
}
