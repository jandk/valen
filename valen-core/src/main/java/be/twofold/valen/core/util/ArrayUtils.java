package be.twofold.valen.core.util;

public final class ArrayUtils {
    private ArrayUtils() {
    }

    // region contains, indexOf, lastIndexOf

    public static boolean contains(byte[] array, int fromIndex, int toIndex, byte value) {
        return indexOf(array, fromIndex, toIndex, value) >= 0;
    }

    public static boolean contains(short[] array, int fromIndex, int toIndex, short value) {
        return indexOf(array, fromIndex, toIndex, value) >= 0;
    }

    public static boolean contains(int[] array, int fromIndex, int toIndex, int value) {
        return indexOf(array, fromIndex, toIndex, value) >= 0;
    }

    public static boolean contains(long[] array, int fromIndex, int toIndex, long value) {
        return indexOf(array, fromIndex, toIndex, value) >= 0;
    }

    public static boolean contains(float[] array, int fromIndex, int toIndex, float value) {
        return indexOf(array, fromIndex, toIndex, value) >= 0;
    }

    public static boolean contains(double[] array, int fromIndex, int toIndex, double value) {
        return indexOf(array, fromIndex, toIndex, value) >= 0;
    }

    public static boolean contains(char[] array, int fromIndex, int toIndex, char value) {
        return indexOf(array, fromIndex, toIndex, value) >= 0;
    }

    public static boolean contains(boolean[] array, int fromIndex, int toIndex, boolean value) {
        return indexOf(array, fromIndex, toIndex, value) >= 0;
    }


    public static int indexOf(byte[] array, int fromIndex, int toIndex, byte value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex; i < toIndex; i++) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(short[] array, int fromIndex, int toIndex, short value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex; i < toIndex; i++) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(int[] array, int fromIndex, int toIndex, int value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex; i < toIndex; i++) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(long[] array, int fromIndex, int toIndex, long value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex; i < toIndex; i++) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(float[] array, int fromIndex, int toIndex, float value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex; i < toIndex; i++) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(double[] array, int fromIndex, int toIndex, double value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex; i < toIndex; i++) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(char[] array, int fromIndex, int toIndex, char value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex; i < toIndex; i++) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(boolean[] array, int fromIndex, int toIndex, boolean value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex; i < toIndex; i++) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }


    public static int lastIndexOf(byte[] array, int fromIndex, int toIndex, byte value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(short[] array, int fromIndex, int toIndex, short value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(int[] array, int fromIndex, int toIndex, int value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(long[] array, int fromIndex, int toIndex, long value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(float[] array, int fromIndex, int toIndex, float value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(double[] array, int fromIndex, int toIndex, double value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(char[] array, int fromIndex, int toIndex, char value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(boolean[] array, int fromIndex, int toIndex, boolean value) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    // endregion contains, indexOf, lastIndexOf

    // region hashCode, toString

    public static int hashCode(byte[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + hashCode(array[i]);
        }
        return result;
    }

    public static int hashCode(short[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + hashCode(array[i]);
        }
        return result;
    }

    public static int hashCode(int[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + hashCode(array[i]);
        }
        return result;
    }

    public static int hashCode(long[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + hashCode(array[i]);
        }
        return result;
    }

    public static int hashCode(float[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + hashCode(array[i]);
        }
        return result;
    }

    public static int hashCode(double[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + hashCode(array[i]);
        }
        return result;
    }

    public static int hashCode(char[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + hashCode(array[i]);
        }
        return result;
    }

    public static int hashCode(boolean[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + hashCode(array[i]);
        }
        return result;
    }


    public static String toString(byte[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        if (fromIndex == toIndex) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }

    public static String toString(short[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        if (fromIndex == toIndex) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }

    public static String toString(int[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        if (fromIndex == toIndex) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }

    public static String toString(long[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        if (fromIndex == toIndex) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }

    public static String toString(float[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        if (fromIndex == toIndex) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }

    public static String toString(double[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        if (fromIndex == toIndex) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }

    public static String toString(char[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        if (fromIndex == toIndex) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }

    public static String toString(boolean[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        if (fromIndex == toIndex) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }

    // endregion hashCode, toString

    // region Helpers

    private static boolean equals(byte array, byte b) {
        return array == b;
    }

    private static boolean equals(short array, short b) {
        return array == b;
    }

    private static boolean equals(int array, int b) {
        return array == b;
    }

    private static boolean equals(long array, long b) {
        return array == b;
    }

    private static boolean equals(float array, float b) {
        return Float.floatToIntBits(array) == Float.floatToIntBits(b);
    }

    private static boolean equals(double array, double b) {
        return Double.doubleToLongBits(array) == Double.doubleToLongBits(b);
    }

    private static boolean equals(char array, char b) {
        return array == b;
    }

    private static boolean equals(boolean array, boolean b) {
        return array == b;
    }

    private static int hashCode(byte value) {
        return Byte.hashCode(value);
    }

    private static int hashCode(short value) {
        return Short.hashCode(value);
    }

    private static int hashCode(int value) {
        return Integer.hashCode(value);
    }

    private static int hashCode(long value) {
        return Long.hashCode(value);
    }

    private static int hashCode(float value) {
        return Float.hashCode(value);
    }

    private static int hashCode(double value) {
        return Double.hashCode(value);
    }

    private static int hashCode(char value) {
        return Character.hashCode(value);
    }

    private static int hashCode(boolean value) {
        return Boolean.hashCode(value);
    }

    // endregion Helpers

}
