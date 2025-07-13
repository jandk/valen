package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;

import java.nio.*;

final class ByteBufferBinaryReader implements BinaryReader {
    private final ByteBuffer buffer;

    ByteBufferBinaryReader(ByteBuffer buffer) {
        this.buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public void read(ByteBuffer dst) {
        Buffers.copy(buffer, dst);
    }

    @Override
    public long size() {
        return buffer.limit();
    }

    @Override
    public long position() {
        return buffer.position();
    }

    @Override
    public BinaryReader position(long pos) {
        buffer.position(Math.toIntExact(pos));
        return this;
    }

    @Override
    public byte readByte() {
        return buffer.get();
    }

    @Override
    public short readShort() {
        return buffer.getShort();
    }

    @Override
    public int readInt() {
        return buffer.getInt();
    }

    @Override
    public long readLong() {
        return buffer.getLong();
    }

    @Override
    public float readFloat() {
        return buffer.getFloat();
    }

    @Override
    public double readDouble() {
        return buffer.getDouble();
    }
}
