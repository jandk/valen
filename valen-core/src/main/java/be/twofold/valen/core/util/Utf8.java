package be.twofold.valen.core.util;

import java.util.*;

/**
 * Small UTF-8 utilities.
 * <p>
 * Provides methods to:
 * - Compute the number of bytes required to encode a CharSequence as UTF-8.
 * - Validate whether a byte array slice is well-formed UTF-8.
 */
public final class Utf8 {
    private Utf8() {
    }

    /**
     * Returns the number of bytes needed to encode the given character sequence in UTF-8.
     *
     * @param cs the character sequence
     * @return the UTF-8 byte length
     * @throws IllegalArgumentException if the input contains an unpaired surrogate
     */
    public static int byteLength(CharSequence cs) {
        return byteLength(cs, 0, cs.length());
    }

    /**
     * Returns the UTF-8 byte length for a sub-sequence of the given character sequence.
     *
     * @param cs        the character sequence
     * @param fromIndex inclusive start index
     * @param toIndex   exclusive end index
     * @return the UTF-8 byte length of the specified range
     * @throws IndexOutOfBoundsException if the range is invalid
     * @throws IllegalArgumentException  if the range contains an unpaired surrogate
     */
    public static int byteLength(CharSequence cs, int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, cs.length());

        int length = toIndex - fromIndex;
        for (int index = fromIndex; index < toIndex; index++) {
            char ch = cs.charAt(index);
            if (ch < 0x80) {
                continue;
            } else if (ch < 0x800) {
                length++;
            } else {
                length += 2;
                if (Character.isHighSurrogate(ch)) {
                    int cp = Character.codePointAt(cs, index);
                    if (cp == ch) {
                        throw new IllegalArgumentException("Unpaired surrogate at index " + index);
                    }
                    index++;
                }
            }
        }
        return length;
    }

    /**
     * Checks whether the entire byte array is a well-formed UTF-8 sequence.
     *
     * @param bytes the bytes to check
     * @return true if valid UTF-8, false otherwise
     */
    public static boolean isValid(byte[] bytes) {
        return isValid(bytes, 0, bytes.length);
    }

    /**
     * Checks whether a slice of the byte array is a well-formed UTF-8 sequence.
     *
     * @param array     the byte array
     * @param fromIndex inclusive start index
     * @param toIndex   exclusive end index
     * @return true if the slice is valid UTF-8, false otherwise
     * @throws IndexOutOfBoundsException if the indices are out of bounds
     */
    public static boolean isValid(byte[] array, int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, array.length);

        int index = fromIndex;
        while (true) {
            int b0;
            do {
                if (index >= toIndex) {
                    return true;
                }
                b0 = array[index++];
            } while (b0 >= 0);

            if (b0 < (byte) 0xE0) { // 2-byte
                if (index >= toIndex) {
                    return false;
                }

                if (b0 < (byte) 0xC2                 // overlong
                    || array[index++] >= (byte) 0xC0 // continuation
                ) {
                    return false;
                }
            } else if (b0 < (byte) 0xF0) { // 3-byte
                if (index + 1 >= toIndex) {
                    return false;
                }

                int b1 = array[index++];
                if (b1 >= (byte) 0xC0                           // continuation
                    || (b0 == (byte) 0xE0 && b1 < (byte) 0xA0)  // overlong
                    || (b0 == (byte) 0xED && b1 >= (byte) 0xA0) // surrogate
                    || array[index++] >= (byte) 0xC0            // continuation
                ) {
                    return false;
                }
            } else { // 4-byte
                if (index + 2 >= toIndex) {
                    return false;
                }

                byte b1 = array[index++];
                if (b1 >= (byte) 0xC0                           // continuation
                    || (b0 == (byte) 0xF0 && b1 < (byte) 0x90)  // overlong
                    || (b0 == (byte) 0xF4 && b1 >= (byte) 0x90) // > max cp
                    || (b0 >= (byte) 0xF5)                      // > max cp
                    || array[index++] >= (byte) 0xC0            // continuation
                    || array[index++] >= (byte) 0xC0            // continuation
                ) {
                    return false;
                }
            }
        }
    }
}
