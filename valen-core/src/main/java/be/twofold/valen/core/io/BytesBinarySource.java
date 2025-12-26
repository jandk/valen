package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

final class BytesBinarySource extends BinarySource {
    private final Bytes bytes;
    private int position = 0;

    BytesBinarySource(Bytes bytes) {
        super(bytes.length());
        this.bytes = Check.nonNull(bytes, "bytes");
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public BytesBinarySource position(long position) {
        this.position = Check.position(Math.toIntExact(position), bytes.length(), "position");
        return this;
    }

    @Override
    public void readBytes(Bytes.Mutable target) {
        bytes.slice(position, target.length()).copyTo(target, 0);
        position += target.length();
    }

    @Override
    public byte readByte() {
        byte result = bytes.get(position);
        position += Byte.BYTES;
        return result;
    }

    @Override
    public short readShort() {
        var result = bytes.getShort(position);
        position += Short.BYTES;
        return bigEndian ? Short.reverseBytes(result) : result;
    }

    @Override
    public int readInt() {
        var result = bytes.getInt(position);
        position += Integer.BYTES;
        return bigEndian ? Integer.reverseBytes(result) : result;
    }

    @Override
    public long readLong() {
        var result = bytes.getLong(position);
        position += Long.BYTES;
        return bigEndian ? Long.reverseBytes(result) : result;
    }

    @Override
    public void close() {
        // do nothing
    }
}
