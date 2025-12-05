package be.twofold.valen.core.io;

import be.twofold.valen.core.util.collect.*;

import java.io.*;

final class BytesBinaryReader implements BinaryReader {
    private final Bytes bytes;
    private int position = 0;

    BytesBinaryReader(Bytes bytes) {
        this.bytes = bytes;
    }

    @Override
    public void read(MutableBytes dst) throws IOException {
        if (dst.length() > bytes.length() - position) {
            throw new EOFException();
        }
        bytes.slice(position, dst.length()).copyTo(dst, 0);
        position += dst.length();
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
        byte result = bytes.get(position);
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
}
