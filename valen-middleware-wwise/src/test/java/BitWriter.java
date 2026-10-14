import java.io.*;
import java.nio.*;
import java.util.*;

public final class BitWriter implements Closeable, Flushable {
    private final OutputStream out;
    private final boolean bigEndian;

    private long bitBuf;
    private int bitCount;
    private int bitsWritten;

    public BitWriter(OutputStream out, ByteOrder order) {
        this.out = Objects.requireNonNull(out);
        this.bigEndian = Objects.requireNonNull(order) == ByteOrder.BIG_ENDIAN;
    }

    public int bitsWritten() {
        return bitsWritten;
    }

    public void write(int count, int value) throws IOException {
        if (count < 0 || count > 32) {
            throw new IllegalArgumentException("count must be between 0 and 32 inclusive");
        }
        write(count, (long) value);
    }

    public void write(int count, long value) throws IOException {
        if (count < 0 || count > 57) {
            throw new IllegalArgumentException("count must be between 0 and 57 inclusive");
        }
        drain(count);
        pokeBits(count, value);
        advance(count);
    }

    public void writeBool(boolean value) throws IOException {
        write(1, value ? 1 : 0);
    }

    private void drain(int count) throws IOException {
        while (bitCount + count > 64) {
            writeByte();
        }
    }

    private void writeByte() throws IOException {
        byte write;
        if (bigEndian) {
            write = (byte) (bitBuf >>> 56);
            bitBuf <<= 8;
        } else {
            write = (byte) (bitBuf);
            bitBuf >>>= 8;
        }
        out.write(write);
        bitCount -= 8;
    }

    private void pokeBits(int count, long value) {
        assert bitCount + count <= 64;
        value &= (1L << count) - 1;
        if (bigEndian) {
            bitBuf |= value << (64 - bitCount - count);
        } else {
            bitBuf |= value << bitCount;
        }
    }

    private void advance(int count) {
        assert bitCount + count <= 64;
        bitCount += count;
        bitsWritten += count;
    }

    @Override
    public void flush() throws IOException {
        while (bitCount >= 8) {
            writeByte();
        }
        if (bitCount > 0) {
            writeByte();
            bitCount = 0;
        }
        bitBuf = 0;
        out.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }
}
