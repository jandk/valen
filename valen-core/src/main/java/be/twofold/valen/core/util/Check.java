package be.twofold.valen.core.util;

import java.util.*;
import java.util.function.*;

/**
 * Static utility methods for validating arguments, state, and indices.
 */
public final class Check {
    private Check() {
        throw new AssertionError();
    }

    /**
     * Checks that the specified object reference is not {@code null} and throws a customized {@link NullPointerException} if it is.
     *
     * @param obj   the object reference to check for nullity
     * @param param the parameter name to include in the exception message
     * @param <T>   the type of the reference
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T nonNull(T obj, String param) {
        return Objects.requireNonNull(obj, () -> "'" + param + "' must not be null");
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param condition a boolean expression
     * @param message   the exception message to use if the check fails
     * @throws IllegalArgumentException if {@code condition} is false
     */
    public static void argument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param condition       a boolean expression
     * @param messageSupplier supplier of the exception message to use if the check fails
     * @throws IllegalArgumentException if {@code condition} is false
     */
    public static void argument(boolean condition, Supplier<String> messageSupplier) {
        if (!condition) {
            throw new IllegalArgumentException(messageSupplier == null ? null : messageSupplier.get());
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance.
     *
     * @param condition a boolean expression
     * @param message   the exception message to use if the check fails
     * @throws IllegalStateException if {@code condition} is false
     */
    public static void state(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance.
     *
     * @param condition       a boolean expression
     * @param messageSupplier supplier of the exception message to use if the check fails
     * @throws IllegalStateException if {@code condition} is false
     */
    public static void state(boolean condition, Supplier<String> messageSupplier) {
        if (!condition) {
            throw new IllegalStateException(messageSupplier == null ? null : messageSupplier.get());
        }
    }

    /**
     * Checks if the {@code index} is within the bounds of the range from {@code 0} (inclusive) to {@code size} (exclusive).
     *
     * @param index the index
     * @param size  the upper-bound (exclusive) of the range
     * @return {@code index} if it is within bounds
     * @throws IndexOutOfBoundsException if the {@code index} is out of bounds
     */
    public static int index(int index, int size) {
        return Objects.checkIndex(index, size);
    }

    /**
     * Checks if the {@code index} is within the bounds of the range from {@code 0} (inclusive) to {@code size} (exclusive).
     *
     * @param index the index
     * @param size  the upper-bound (exclusive) of the range
     * @return {@code index} if it is within bounds
     * @throws IndexOutOfBoundsException if the {@code index} is out of bounds
     */
    public static long index(long index, long size) {
        return Objects.checkIndex(index, size);
    }

    /**
     * Checks that the given position is valid and within the specified limit.
     *
     * @param position the position to check
     * @param limit    the upper bound (inclusive)
     * @param param    the name of the parameter for the error message
     * @return {@code position} if it is valid
     * @throws IllegalArgumentException if {@code position} is negative or > limit
     */
    public static int position(int position, int limit, String param) {
        if (position < 0 || position > limit) {
            throw new IllegalArgumentException("'" + param + "' must be a valid position (0 <= position <= " + limit + ")");
        }
        return position;
    }

    /**
     * Checks that the given position is valid and within the specified limit.
     *
     * @param position the position to check
     * @param limit    the upper bound (inclusive)
     * @param param    the name of the parameter for the error message
     * @return {@code position} if it is valid
     * @throws IllegalArgumentException if {@code position} is negative or > limit
     */
    public static long position(long position, long limit, String param) {
        if (position < 0 || position > limit) {
            throw new IllegalArgumentException("'" + param + "' must be a valid position (0 <= position <= " + limit + ")");
        }
        return position;
    }

    /**
     * Checks if the sub-range from {@code fromIndex} (inclusive) to {@code toIndex} (exclusive) is within the bounds of range from {@code 0} (inclusive) to {@code size} (exclusive).
     *
     * @param fromIndex the lower-bound (inclusive) of the sub-range
     * @param toIndex   the upper-bound (exclusive) of the sub-range
     * @param size      the upper-bound (exclusive) of the range
     * @return {@code fromIndex} if the sub-range is within bounds
     * @throws IndexOutOfBoundsException if the sub-range is out of bounds
     */
    public static int fromToIndex(int fromIndex, int toIndex, int size) {
        return Objects.checkFromToIndex(fromIndex, toIndex, size);
    }

    /**
     * Checks if the sub-range from {@code fromIndex} (inclusive) to {@code toIndex} (exclusive) is within the bounds of range from {@code 0} (inclusive) to {@code size} (exclusive).
     *
     * @param fromIndex the lower-bound (inclusive) of the sub-range
     * @param toIndex   the upper-bound (exclusive) of the sub-range
     * @param size      the upper-bound (exclusive) of the range
     * @return {@code fromIndex} if the sub-range is within bounds
     * @throws IndexOutOfBoundsException if the sub-range is out of bounds
     */
    public static long fromToIndex(long fromIndex, long toIndex, long size) {
        return Objects.checkFromToIndex(fromIndex, toIndex, size);
    }

    /**
     * Checks if the sub-range from {@code fromIndex} (inclusive) to {@code fromIndex + size} (exclusive) is within the bounds of range from {@code 0} (inclusive) to {@code length} (exclusive).
     *
     * @param fromIndex the lower-bound (inclusive) of the sub-interval
     * @param size      the size of the sub-range
     * @param length    the upper-bound (exclusive) of the range
     * @return {@code fromIndex} if the sub-range is within bounds
     * @throws IndexOutOfBoundsException if the sub-range is out of bounds
     */
    public static int fromIndexSize(int fromIndex, int size, int length) {
        return Objects.checkFromIndexSize(fromIndex, size, length);
    }

    /**
     * Checks if the sub-range from {@code fromIndex} (inclusive) to {@code fromIndex + size} (exclusive) is within the bounds of range from {@code 0} (inclusive) to {@code length} (exclusive).
     *
     * @param fromIndex the lower-bound (inclusive) of the sub-interval
     * @param size      the size of the sub-range
     * @param length    the upper-bound (exclusive) of the range
     * @return {@code fromIndex} if the sub-range is within bounds
     * @throws IndexOutOfBoundsException if the sub-range is out of bounds
     */
    public static long fromIndexSize(long fromIndex, long size, long length) {
        return Objects.checkFromIndexSize(fromIndex, size, length);
    }

    /**
     * Checks that the given value is strictly positive.
     *
     * @param value the value to check
     * @param param the name of the parameter for the error message
     * @return {@code value} if it is greater than 0
     * @throws IllegalArgumentException if {@code value} is 0 or less
     */
    public static int positive(int value, String param) {
        if (value <= 0) {
            throw new IllegalArgumentException("'" + param + "' must be greater than 0");
        }
        return value;
    }

    /**
     * Checks that the given value is strictly positive.
     *
     * @param value the value to check
     * @param param the name of the parameter for the error message
     * @return {@code value} if it is greater than 0
     * @throws IllegalArgumentException if {@code value} is 0 or less
     */
    public static long positive(long value, String param) {
        if (value <= 0) {
            throw new IllegalArgumentException("'" + param + "' must be greater than 0");
        }
        return value;
    }

    /**
     * Checks that the given value is positive or zero.
     *
     * @param value the value to check
     * @param param the name of the parameter for the error message
     * @return {@code value} if it is 0 or greater
     * @throws IllegalArgumentException if {@code value} is less than 0
     */
    public static int positiveOrZero(int value, String param) {
        if (value < 0) {
            throw new IllegalArgumentException("'" + param + "' must be greater than or equal to 0");
        }
        return value;
    }

    /**
     * Checks that the given value is positive or zero.
     *
     * @param value the value to check
     * @param param the name of the parameter for the error message
     * @return {@code value} if it is 0 or greater
     * @throws IllegalArgumentException if {@code value} is less than 0
     */
    public static long positiveOrZero(long value, String param) {
        if (value < 0) {
            throw new IllegalArgumentException("'" + param + "' must be greater than or equal to 0");
        }
        return value;
    }
}
