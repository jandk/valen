package be.twofold.valen.core.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public final class ChannelDataSource extends DataSource {
    private final ByteBuffer buffer = ByteBuffer
        .allocate(8192)
        .order(ByteOrder.LITTLE_ENDIAN)
        .limit(0);

    private final SeekableByteChannel channel;
    private final long lim;
    private long pos;

    public ChannelDataSource(SeekableByteChannel channel) throws IOException {
        this(channel, 0, channel.size());
    }

    public ChannelDataSource(SeekableByteChannel channel, long offset, long length) throws IOException {
        Objects.checkFromIndexSize(offset, length, channel.size());
        this.channel = channel;
        this.pos = offset;
        this.lim = offset + length;
    }

    @Override
    public void readBytes(byte[] dst, int off, int len) throws IOException {
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
        if (len < buffer.capacity()) {
            refill();
            if (len > buffer.remaining()) {
                throw new EOFException();
            }
            buffer.get(dst, off, len);
            return;
        }

        // If we can't fit the remaining bytes in the buffer, read directly into the destination
        long end = pos + buffer.position() + len;
        if (end > lim) {
            throw new EOFException();
        }
        readInternal(ByteBuffer.wrap(dst, off, len));
        pos = end;
        buffer.limit(0);
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

    //
    // Helper methods
    //

    private void readInternal(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            try {
                if (channel.read(buffer) == -1) {
                    throw new EOFException();
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private void refillWhen(int n) throws EOFException {
        if (buffer.remaining() < n) {
            refill();
            if (buffer.remaining() < n) {
                throw new EOFException("Expected to read " + n + " bytes, but only " + buffer.remaining() + " bytes are available");
            }
        }
    }

    private void refill() {
        buffer.compact();
        long end = Math.min(pos + buffer.remaining(), lim);
        buffer.limit((int) (buffer.position() + end - pos));
        readInternal(buffer);
        buffer.flip();
    }

    public static void main(String[] args) {
        try {
            Path path = Path.of("test.bin");
            Files.write(path, new byte[]{
                1,
                2, 0,
                3, 0, 0, 0,
                4, 0, 0, 0, 0, 0, 0, 0,
                0, 0, (byte) 128, 63,
                0, 0, 0, 0, 0, 0, -16, 63
            });
            var channel = FileChannel.open(path, StandardOpenOption.READ);
            var dataSource = new ChannelDataSource(channel);
            System.out.println(dataSource.readByte());
            System.out.println(dataSource.readShort());
            System.out.println(dataSource.readInt());
            System.out.println(dataSource.readLong());
            System.out.println(dataSource.readFloat());
            System.out.println(dataSource.readDouble());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
