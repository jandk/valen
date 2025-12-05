package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

final class ChannelBinaryReader implements BinaryReader, Closeable {
    private static final int BUFFER_CAPACITY = 8192;
    private final MutableBytes buffer = MutableBytes.allocate(BUFFER_CAPACITY);
    private int buffPos;
    private int buffLim;

    private final SeekableByteChannel channel;
    private final long size;
    private long position = 0;

    ChannelBinaryReader(SeekableByteChannel channel) throws IOException {
        this.channel = channel;
        this.size = channel.size();
    }

    @Override
    public void read(ByteBuffer dst) throws IOException {
        int remaining = remaining();
        if (dst.remaining() <= remaining) {
            int length = dst.remaining();
            dst.put(buffer.slice(buffPos, length).asBuffer());
            buffPos += length;
            return;
        }

        if (remaining > 0) {
            dst.put(buffer.slice(buffPos, remaining).asBuffer());
            buffPos += remaining;
        }

        // If we can fit the remaining bytes in the buffer, do a normal refill and read
        if (dst.remaining() < BUFFER_CAPACITY) {
            refill();
            if (dst.remaining() > remaining()) {
                throw new EOFException();
            }
            int length = dst.remaining();
            dst.put(buffer.slice(buffPos, length).asBuffer());
            buffPos += length;
            return;
        }

        // If we can't fit the remaining bytes in the buffer, read directly into the destination
        long end = position + buffPos + dst.remaining();
        if (end > size) {
            throw new EOFException();
        }
        readInternal(dst);
        position = end;
        buffPos = 0;
        buffLim = 0;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public long position() {
        return position + buffPos;
    }

    @Override
    public BinaryReader position(long pos) throws IOException {
        Check.index(pos, size + 1);

        if (pos >= position && pos < position + buffLim) {
            buffPos = (int) (pos - position);
        } else {
            position = pos;
            buffPos = 0;
            buffLim = 0;
            channel.position(pos);
        }
        return this;
    }

    @Override
    public byte readByte() throws IOException {
        refillWhen(Byte.BYTES);
        byte result = buffer.get(buffPos);
        buffPos += Byte.BYTES;
        return result;
    }

    @Override
    public short readShort() throws IOException {
        refillWhen(Short.BYTES);
        short result = buffer.getShort(buffPos);
        buffPos += Short.BYTES;
        return result;
    }

    @Override
    public int readInt() throws IOException {
        refillWhen(Integer.BYTES);
        int result = buffer.getInt(buffPos);
        buffPos += Integer.BYTES;
        return result;
    }

    @Override
    public long readLong() throws IOException {
        refillWhen(Long.BYTES);
        long result = buffer.getLong(buffPos);
        buffPos += Long.BYTES;
        return result;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    //
    // Helper methods
    //

    private void refillWhen(int n) throws IOException {
        if (remaining() < n) {
            refill();
            if (remaining() < n) {
                throw new EOFException("Expected to read " + n + " bytes, but only " + remaining() + " bytes are available");
            }
        }
    }

    private void refill() throws IOException {
        long start = position + buffPos;
        long end = Math.min(start + BUFFER_CAPACITY, size);
        int length = remaining();

        position = start;
        buffer.slice(buffPos, length).copyTo(buffer, 0);
        buffLim = Math.toIntExact(end - start);
        buffPos = length;
        readInternal();
        buffLim = buffPos;
        buffPos = 0;
    }

    private void readInternal(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new EOFException();
            }
        }
    }

    private void readInternal() throws IOException {
        while (remaining() > 0) {
            var buffer = this.buffer.slice(buffPos, remaining()).asMutableBuffer();
            var read = channel.read(buffer);
            if (read == -1) {
                throw new EOFException();
            }
            buffPos += read;
        }
    }

    private int remaining() {
        return buffLim - buffPos;
    }
}
