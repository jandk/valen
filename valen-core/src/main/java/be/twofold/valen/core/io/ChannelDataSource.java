package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

final class ChannelDataSource implements DataSource, Closeable {
    private final ByteBuffer buffer = Buffers
        .allocate(8192)
        .limit(0);

    private final SeekableByteChannel channel;
    private final long size;
    private long position = 0;

    ChannelDataSource(SeekableByteChannel channel) throws IOException {
        this.channel = channel;
        this.size = channel.size();
    }

    @Override
    public void read(ByteBuffer dst) throws IOException {
        int remaining = buffer.remaining();
        if (dst.remaining() <= remaining) {
            Buffers.copy(buffer, dst);
            return;
        }

        if (remaining > 0) {
            Buffers.copy(buffer, dst, remaining);
        }

        // If we can fit the remaining bytes in the buffer, do a normal refill and read
        if (dst.remaining() < buffer.capacity()) {
            refill();
            if (dst.remaining() > buffer.remaining()) {
                throw new EOFException();
            }
            Buffers.copy(buffer, dst);
            return;
        }

        // If we can't fit the remaining bytes in the buffer, read directly into the destination
        long end = position + buffer.position() + dst.remaining();
        if (end > size) {
            throw new EOFException();
        }
        readInternal(dst);
        position = end;
        buffer.limit(0);
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public long position() {
        return position + buffer.position();
    }

    @Override
    public void position(long pos) throws IOException {
        Check.index(pos, size + 1);

        if (pos >= position && pos < position + buffer.limit()) {
            buffer.position((int) (pos - position));
        } else {
            position = pos;
            buffer.limit(0);
            channel.position(pos);
        }
    }

    @Override
    public byte readByte() throws IOException {
        refillWhen(Byte.BYTES);
        return buffer.get();
    }

    @Override
    public short readShort() throws IOException {
        refillWhen(Short.BYTES);
        return buffer.getShort();
    }

    @Override
    public int readInt() throws IOException {
        refillWhen(Integer.BYTES);
        return buffer.getInt();
    }

    @Override
    public long readLong() throws IOException {
        refillWhen(Long.BYTES);
        return buffer.getLong();
    }

    @Override
    public float readFloat() throws IOException {
        refillWhen(Float.BYTES);
        return buffer.getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        refillWhen(Double.BYTES);
        return buffer.getDouble();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    //
    // Helper methods
    //

    private void refillWhen(int n) throws IOException {
        if (buffer.remaining() < n) {
            refill();
            if (buffer.remaining() < n) {
                throw new EOFException("Expected to read " + n + " bytes, but only " + buffer.remaining() + " bytes are available");
            }
        }
    }

    private void refill() throws IOException {
        long start = position + buffer.position();
        long end = Math.min(start + buffer.capacity(), size);

        position = start;
        buffer.compact();
        buffer.limit(Math.toIntExact(end - start));
        readInternal(buffer);
        buffer.flip();
    }

    private void readInternal(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new EOFException();
            }
        }
    }
}
