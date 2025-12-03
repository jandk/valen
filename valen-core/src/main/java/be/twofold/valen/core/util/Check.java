package be.twofold.valen.core.util;

import java.util.*;
import java.util.function.*;

public final class Check {
    private Check() {
        throw new AssertionError();
    }

    public static <T> T notNull(T obj, String param) {
        if (obj == null) {
            throw new NullPointerException(param + " can not be null");
        }
        return obj;
    }

    public static void argument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
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

    public static int index(int index, int length) {
        return Objects.checkIndex(index, length);
    }

    public static long index(long index, long length) {
        return Objects.checkIndex(index, length);
    }

    public static int fromToIndex(int fromIndex, int toIndex, int length) {
        return Objects.checkFromToIndex(fromIndex, toIndex, length);
    }

    public static long fromToIndex(long fromIndex, long toIndex, long length) {
        return Objects.checkFromToIndex(fromIndex, toIndex, length);
    }

    public static int fromIndexSize(int fromIndex, int size, int length) {
        return Objects.checkFromIndexSize(fromIndex, size, length);
    }

    public static long fromIndexSize(long fromIndex, long size, long length) {
        return Objects.checkFromIndexSize(fromIndex, size, length);
    }

    public static int positive(int value, String param) {
        if (value <= 0) {
            throw new IllegalArgumentException(param + " must be greater than 0");
        }
        return value;
    }

    public static int positiveOrZero(int value, String param) {
        if (value < 0) {
            throw new IllegalArgumentException(param + " must be greater than or equal to 0");
        }
        return value;
    }
}
