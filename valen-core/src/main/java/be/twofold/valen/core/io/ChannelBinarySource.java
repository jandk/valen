package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

final class ChannelBinarySource extends BinarySource {
    private static final int BUFFER_CAPACITY = 8192;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY).order(ByteOrder.LITTLE_ENDIAN).limit(0);
    private final SeekableByteChannel channel;
    private long bufferOffset = 0;

    ChannelBinarySource(SeekableByteChannel channel) throws IOException {
        super(Check.nonNull(channel, "channel").size());
        this.channel = channel;
    }

    @Override
    public long position() {
        return bufferOffset + buffer.position();
    }

    @Override
    public BinarySource position(long position) {
        Check.index(position, size + 1);
        if (position >= bufferOffset && position <= bufferOffset + buffer.limit()) {
            buffer.position((int) (position - bufferOffset));
        } else {
            bufferOffset = position;
            buffer.position(0).limit(0);
        }
        return this;
    }

    @Override
    public void readBytes(Bytes.Mutable target) throws IOException {
        int targetOffset = 0;
        int targetRemaining = target.length();
        while (targetRemaining > 0) {
            int fromBuffer = Math.min(targetRemaining, buffer.remaining());
            if (fromBuffer > 0) {
                ByteBuffer sourceView = buffer.duplicate();
                sourceView.limit(sourceView.position() + fromBuffer);
                target.slice(targetOffset, fromBuffer).asMutableBuffer().put(sourceView);
                buffer.position(buffer.position() + fromBuffer);
                targetOffset += fromBuffer;
                targetRemaining -= fromBuffer;
            } else if (targetRemaining >= BUFFER_CAPACITY) {
                long pos = position();
                if (pos + targetRemaining > size) {
                    throw new EOFException();
                }
                channel.position(pos);
                ByteBuffer targetBuf = target.slice(targetOffset, targetRemaining).asMutableBuffer();
                while (targetBuf.hasRemaining()) {
                    if (channel.read(targetBuf) == -1) {
                        throw new EOFException();
                    }
                }
                bufferOffset = pos + targetRemaining;
                buffer.position(0).limit(0);
                return;
            } else {
                refill();
                if (buffer.remaining() == 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public byte readByte() throws IOException {
        ensureRefilled(Byte.BYTES);
        return buffer.get();
    }

    @Override
    public short readShort() throws IOException {
        ensureRefilled(Short.BYTES);
        short result = buffer.getShort();
        return bigEndian ? Short.reverseBytes(result) : result;
    }

    @Override
    public int readInt() throws IOException {
        ensureRefilled(Integer.BYTES);
        int result = buffer.getInt();
        return bigEndian ? Integer.reverseBytes(result) : result;
    }

    @Override
    public long readLong() throws IOException {
        ensureRefilled(Long.BYTES);
        long result = buffer.getLong();
        return bigEndian ? Long.reverseBytes(result) : result;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    private void ensureRefilled(int length) throws IOException {
        if (buffer.remaining() < length) {
            refill();
            if (buffer.remaining() < length) {
                throw new EOFException("Expected to read " + length + " bytes, but only " + buffer.remaining() + " bytes are available");
            }
        }
    }

    private void refill() throws IOException {
        bufferOffset += buffer.position();
        buffer.compact();

        int toRead = (int) Math.min(buffer.remaining(), size - (bufferOffset + buffer.position()));
        if (toRead > 0) {
            channel.position(bufferOffset + buffer.position());
            int limit = buffer.limit();
            buffer.limit(buffer.position() + toRead);
            while (buffer.hasRemaining()) {
                if (channel.read(buffer) == -1) {
                    throw new EOFException();
                }
            }
            buffer.limit(limit);
        }
        buffer.flip();
    }
}
