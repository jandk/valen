package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.channels.*;

final class ChannelBinaryReader implements BinaryReader {
    private static final int BUFFER_CAPACITY = 8192;
    private final Bytes.Mutable buffer = Bytes.Mutable.allocate(BUFFER_CAPACITY);
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
    public void read(Bytes.Mutable dst) throws IOException {
        int buffRem = buffRem();
        int destRem = dst.length();
        if (buffRem >= destRem) {
            buffer.slice(buffPos, destRem).copyTo(dst, 0);
            buffPos += destRem;
            return;
        }

        if (buffRem > 0) {
            buffer.slice(buffPos, buffRem).copyTo(dst, 0);
            buffPos += buffRem;
            destRem -= buffRem;
        }

        // If we can fit the remaining bytes in the buffer, do a normal refill and read
        if (destRem < BUFFER_CAPACITY) {
            refill();
            if (destRem > buffRem()) {
                throw new EOFException();
            }
            buffer.slice(buffPos, destRem).copyTo(dst, dst.length() - destRem);
            buffPos += destRem;
            return;
        }

        // If we can't fit the remaining bytes in the buffer, read directly into the destination
        long end = position + buffPos + destRem;
        if (end > size) {
            throw new EOFException();
        }
        readInternal(dst, dst.length() - destRem);
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
        if (buffRem() < n) {
            refill();
            if (buffRem() < n) {
                throw new EOFException("Expected to read " + n + " bytes, but only " + buffRem() + " bytes are available");
            }
        }
    }

    private void refill() throws IOException {
        long start = position + buffPos;
        long end = Math.min(start + BUFFER_CAPACITY, size);
        int length = buffRem();

        position = start;
        buffer.slice(buffPos, length).copyTo(buffer, 0);
        buffLim = Math.toIntExact(end - start);
        buffLim = readInternal(buffer, length);
        buffPos = 0;
    }

    private int readInternal(Bytes.Mutable dst, int dstPos) throws IOException {
        while (dstPos < dst.length()) {
            var buffer = dst.slice(dstPos).asMutableBuffer();
            var read = channel.read(buffer);
            if (read == -1) {
                throw new EOFException();
            }
            dstPos += read;
        }
        return dstPos;
    }

    private int buffRem() {
        return buffLim - buffPos;
    }
}
