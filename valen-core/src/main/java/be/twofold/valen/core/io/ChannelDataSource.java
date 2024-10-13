package be.twofold.valen.core.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

final class ChannelDataSource extends DataSource {
    private final ByteBuffer buffer = ByteBuffer
        .allocate(8192)
        .order(ByteOrder.LITTLE_ENDIAN)
        .limit(0);

    private final SeekableByteChannel channel;
    private final long offset;
    private final long length;
    private final long lim;
    private long bufPos;

    ChannelDataSource(SeekableByteChannel channel) throws IOException {
        this(channel, 0, channel.size());
    }

    ChannelDataSource(SeekableByteChannel channel, long offset, long length) throws IOException {
        Objects.checkFromIndexSize(offset, length, channel.size());
        this.channel = channel;
        this.offset = offset;
        this.length = length;
        this.bufPos = offset;
        this.lim = offset + length;
    }

    @Override
    public void readBytes(byte[] dst, int off, int len, boolean buffered) throws IOException {
        int remaining = buffer.remaining();
        if (len <= remaining) {
            buffer.get(dst, off, len);
            return;
        }

        if (remaining > 0) {
            buffer.get(dst, off, remaining);
            off += remaining;
            len -= remaining;
        }

        // If we can fit the remaining bytes in the buffer, do a normal refill and read
        if (buffered && len < buffer.capacity()) {
            refill();
            if (len > buffer.remaining()) {
                throw new EOFException();
            }
            buffer.get(dst, off, len);
            return;
        }

        // If we can't fit the remaining bytes in the buffer, read directly into the destination
        long end = bufPos + buffer.position() + len;
        if (end > lim) {
            throw new EOFException();
        }
        readInternal(ByteBuffer.wrap(dst, off, len));
        bufPos = end;
        buffer.limit(0);
    }

    @Override
    public long tell() {
        return bufPos + buffer.position();
    }

    @Override
    public void seek(long pos) throws IOException {
        if (pos > lim) {
            throw new EOFException("Seek position " + pos + " is beyond EOF");
        }

        if (pos >= bufPos && pos < bufPos + buffer.limit()) {
            buffer.position((int) (pos - bufPos));
            return;
        }

        bufPos = pos;
        buffer.limit(0);
        channel.position(pos);
    }

    @Override
    public long size() {
        return length;
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

    private void readInternal(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new EOFException();
            }
        }
    }

    private void refillWhen(int n) throws IOException {
        if (buffer.remaining() < n) {
            refill();
            if (buffer.remaining() < n) {
                throw new EOFException("Expected to read " + n + " bytes, but only " + buffer.remaining() + " bytes are available");
            }
        }
    }

    private void refill() throws IOException {
        long start = bufPos + buffer.position();
        long end = Math.min(start + buffer.capacity(), lim);
        buffer.compact();

        bufPos = start;
        buffer.limit((int) (end - start));
        readInternal(buffer);
        buffer.flip();
    }
}
