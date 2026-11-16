package be.twofold.valen.middleware.wwise.util;

import wtf.reversed.toolbox.util.*;

import java.nio.*;
import java.util.*;

public final class BitWriter {
    final boolean bigEndian;

    private long bitBuf;
    private int bitCount;
    private int bitsWritten;

    private byte[] buffer;
    private int bufferPos = 0;

    public BitWriter(ByteOrder order) {
        this.buffer = new byte[1024];
        this.bigEndian = Check.nonNull(order, "order") == ByteOrder.BIG_ENDIAN;
    }

    public int bitsWritten() {
        return bitsWritten;
    }

    public void write(int count, int value) {
        Check.between(count, 0, 32, "count");
        write(count, (long) value);
    }

    public void write(int count, long value) {
        Check.between(count, 0, 57, "count");
        drain(count);
        pokeBits(count, value);
        advance(count);
    }

    public void writeBool(boolean value) {
        write(1, value ? 1 : 0);
    }

    public byte[] toByteArray() {
        while (bitCount >= 8) {
            writeByte();
        }
        if (bitCount > 0) {
            writeByte();
            bitCount = 0;
        }
        bitBuf = 0;
        return Arrays.copyOf(buffer, bufferPos);
    }

    private void drain(int count) {
        while (bitCount + count > 64) {
            writeByte();
        }
    }

    private void writeByte() {
        byte write;
        if (bigEndian) {
            write = (byte) (bitBuf >>> 56);
            bitBuf <<= 8;
        } else {
            write = (byte) (bitBuf);
            bitBuf >>>= 8;
        }

        if (bufferPos >= buffer.length) {
            buffer = Arrays.copyOf(buffer, buffer.length * 2);
        }
        buffer[bufferPos++] = write;
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

}
