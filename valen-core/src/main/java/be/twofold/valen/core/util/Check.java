package be.twofold.valen.core.util;

import java.util.function.*;

public final class Check {
    private Check() {
        throw new AssertionError();
    }

    public static <T> T notNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static <T> T notNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    public static <T> T notNull(T obj, Supplier<String> messageSupplier) {
        if (obj == null) {
            throw new NullPointerException(messageSupplier == null ? null : messageSupplier.get());
        }
        return obj;
    }

    public static void argument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    public static void argument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void argument(boolean condition, Supplier<String> messageSupplier) {
        if (!condition) {
            throw new IllegalArgumentException(messageSupplier == null ? null : messageSupplier.get());
        }
    }

    public static void state(boolean condition) {
        if (!condition) {
            throw new IllegalStateException();
        }
    }

    public static void state(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static void state(boolean condition, Supplier<String> messageSupplier) {
        if (!condition) {
            throw new IllegalStateException(messageSupplier == null ? null : messageSupplier.get());
        }
    }

    public static int index(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(String.format("Index %s out of bounds for length %s", index, size));
        }
        return index;
    }

    public static int fromToIndex(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > size) {
            throw new IndexOutOfBoundsException(String.format("Range [%s, %s) out of bounds for length %s", fromIndex, toIndex, size));
        }
        return fromIndex;
    }

    public static int fromIndexSize(int fromIndex, int size, int length) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            throw new IndexOutOfBoundsException(String.format("Range [%s, %<s + %s) out of bounds for length %s", fromIndex, size, length));
        }
        return fromIndex;
    }

    public static long fromIndexSize(long fromIndex, long size, long length) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            throw new IndexOutOfBoundsException(String.format("Range [%s, %<s + %s) out of bounds for length %s", fromIndex, size, length));
        }
        return fromIndex;
    }
}
