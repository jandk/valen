package be.twofold.valen.middleware.wwise.util;

import wtf.reversed.toolbox.util.*;

import java.lang.invoke.*;
import java.nio.*;
import java.util.*;

/**
 * Reads variable-width bit fields from a {@code byte[]}. Supports both little and big endian bit readers.
 */
public final class BitReader {
    private static final VarHandle VH_LONG_LE = MethodHandles
        .byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    private final byte[] source;
    private final int offset;
    private final int end;
    private final boolean bigEndian;
    private int position;
    private long buffer;
    private int bitCount;

    public BitReader(byte[] source, ByteOrder order) {
        this(source, 0, source.length, order);
    }

    public BitReader(byte[] source, int offset, int length, ByteOrder order) {
        Objects.checkFromIndexSize(offset, length, source.length);
        this.source = source;
        this.offset = offset;
        this.end = offset + length;
        this.bigEndian = Objects.requireNonNull(order) == ByteOrder.BIG_ENDIAN;
        this.position = offset;
    }

    /**
     * Discards any unread bits within the current byte, advancing to the next byte boundary.
     */
    public void alignToByte() {
        consume(bitCount & 7);
    }

    /**
     * Returns the number of bits consumed from this source since it was created.
     * <p>
     * Unaffected by {@link #peek} and {@link #peekLong}.
     */
    public int bitsRead() {
        return (position - offset) * 8 - bitCount;
    }

    /**
     * Returns the number of bits still available to read.
     */
    public int bitsRemaining() {
        return bitCount + (end - position) * 8;
    }

    /**
     * Returns {@code true} if we read past the end of the source.
     */
    public boolean isOverrun() {
        return bitCount < 0;
    }

    /**
     * Consumes and returns the next {@code count} bits as an unsigned value. Missing bits read as zero.
     *
     * @param count number of bits to read, in {@code [0, 32]}
     */
    public int read(int count) {
        if (count < 0 || count > 32) {
            throw new IllegalArgumentException("count must be between 0 and 32 inclusive");
        }
        return (int) readLong(count);
    }

    /**
     * Consumes and returns the next {@code count} bits as an unsigned long value. Missing bits read as zero.
     *
     * @param count number of bits to read, in {@code [0, 57]}
     */
    public long readLong(int count) {
        if (count < 0 || count > 57) {
            throw new IllegalArgumentException("count must be between 0 and 57 inclusive");
        }
        refill(count);
        long result = peekBits(count);
        consume(count);
        return result;
    }

    /**
     * Consumes and returns the next single bit as {@code true} or {@code false}.
     */
    public boolean readBool() {
        return readLong(1) != 0;
    }

    /**
     * Returns the next {@code count} bits without advancing the cursor. Missing bits read as zero.
     *
     * @param count number of bits to peek, in {@code [0, 32]}
     */
    public int peek(int count) {
        if (count < 0 || count > 32) {
            throw new IllegalArgumentException("count must be between 0 and 32 inclusive");
        }
        return (int) peekLong(count);
    }

    /**
     * Returns the next {@code count} bits without advancing the cursor. Missing bits read as zero.
     *
     * @param count number of bits to peek, in {@code [0, 57]}
     */
    public long peekLong(int count) {
        if (count < 0 || count > 57) {
            throw new IllegalArgumentException("count must be between 0 and 57 inclusive");
        }
        refill(count);
        return peekBits(count);
    }

    /**
     * Discards the next {@code count} bits.
     * <p>
     * If you skip more than the remaining bits in the source, no exception will be thrown.
     *
     * @param count number of bits to skip, in {@code [0, 57]}
     */
    public void skip(int count) {
        if (count < 0 || count > 57) {
            throw new IllegalArgumentException("count must be between 0 and 57 inclusive");
        }
        refill(count);
        consume(count);
    }

    public void copyTo(BitWriter writer, int count) {
        Check.argument(bigEndian == writer.bigEndian,
            "BitReader and BitWriter must have the same endianness");

        while (count > 0) {
            int amount = Math.min(count, 57);
            writer.write(amount, readLong(amount));
            count -= amount;
        }
    }

    private long peekBits(int count) {
        if (count == 0) {
            return 0;
        }
        if (bigEndian) {
            return buffer >>> (64 - count);
        } else {
            return buffer & (-1L >>> (64 - count));
        }
    }

    private void consume(int count) {
        if (bigEndian) {
            buffer <<= count;
        } else {
            buffer >>>= count;
        }
        bitCount -= count;
    }

    private void refill(int count) {
        if (bitCount < count) {
            if (bitCount <= 56 && position <= end - 8) {
                refillLong();
            }
            // Handle the end
            while (bitCount < count && position < end) {
                refillByte(Byte.toUnsignedLong(source[position++]));
            }
        }
    }

    private void refillLong() {
        // Read 8 bytes and flip if we need to
        long value = (long) VH_LONG_LE.get(source, position);
        position += 8;
        if (bigEndian) {
            value = Long.reverseBytes(value);
        }

        // Shift a multiple of bytes over for the new bits
        int shift = (bitCount + 7) & ~7;
        if (bigEndian) {
            buffer |= (value & (-1L << shift)) >>> bitCount;
        } else {
            buffer |= (value & (-1L >>> shift)) << bitCount;
        }

        // Adjust the position, in case we read too much
        int usedBytes = (64 - bitCount) >>> 3;
        position -= 8 - usedBytes;
        bitCount += usedBytes << 3;
    }

    private void refillByte(long value) {
        if (bigEndian) {
            buffer |= value << (56 - bitCount);
        } else {
            buffer |= value << bitCount;
        }
        bitCount += 8;
    }
}
