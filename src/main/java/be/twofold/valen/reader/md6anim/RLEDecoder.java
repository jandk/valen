package be.twofold.valen.reader.md6anim;

import be.twofold.valen.*;

import java.util.*;

public class RLEDecoder {

    public static byte[] decodeRLE(BetterBuffer buffer, int numJoints) {
        int size = buffer.getByte();
        int length = Math.min(size, numJoints);

        byte[] result = new byte[length];
        for (int o = 0; o < length; ) {
            int count = buffer.getByte();
            if ((count & 0x80) != 0) {
                throw new UnsupportedOperationException();
            }

            int value = buffer.getByte();
            for (int i = 0; i < count; i++) {
                result[o++] = (byte) (value + i);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        byte[] input = {0x1E, 0x02, 0x07, 0x03, 0x0A, 0x01, 0x0E, 0x06, 0x13, 0x01, 0x1A, 0x01, 0x27, 0x02, 0x2D, 0x06, 0x30, 0x02, 0x3C, 0x06, 0x3F};
        byte[] expected = {0x07, 0x08, 0x0a, 0x0b, 0x0c, 0x0e, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x1a, 0x27, 0x2d, 0x2e, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x3c, 0x3d, 0x3f, 0x40, 0x41, 0x42, 0x43, 0x44};
        byte[] actual = decodeRLE(BetterBuffer.wrap(input), 0x20);
        System.out.println("Arrays.equals(expected, actual) = " + Arrays.equals(expected, actual));
    }
}
