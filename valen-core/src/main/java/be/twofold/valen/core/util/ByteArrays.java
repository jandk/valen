package be.twofold.valen.core.util;

import java.lang.invoke.*;
import java.nio.*;

public final class ByteArrays {
    private static final VarHandle SHORT_LE = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle SHORT_BE = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle INT_LE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle INT_BE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_LE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_BE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle FLOAT_LE = MethodHandles.byteArrayViewVarHandle(float[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle FLOAT_BE = MethodHandles.byteArrayViewVarHandle(float[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle DOUBLE_LE = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle DOUBLE_BE = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.BIG_ENDIAN);

    private ByteArrays() {
    }

    public static short getShort(byte[] array, int offset, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? SHORT_LE : SHORT_BE;
        return (short) handle.get(array, offset);
    }

    public static int getInt(byte[] array, int offset, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? INT_LE : INT_BE;
        return (int) handle.get(array, offset);
    }

    public static long getLong(byte[] array, int offset, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? LONG_LE : LONG_BE;
        return (long) handle.get(array, offset);
    }

    public static float getFloat(byte[] array, int offset, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? FLOAT_LE : FLOAT_BE;
        return (float) handle.get(array, offset);
    }

    public static double getDouble(byte[] array, int offset, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? DOUBLE_LE : DOUBLE_BE;
        return (double) handle.get(array, offset);
    }


    public static void setShort(byte[] array, int offset, short value, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? SHORT_LE : SHORT_BE;
        handle.set(array, offset, value);
    }

    public static void setInt(byte[] array, int offset, int value, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? INT_LE : INT_BE;
        handle.set(array, offset, value);
    }

    public static void setLong(byte[] array, int offset, long value, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? LONG_LE : LONG_BE;
        handle.set(array, offset, value);
    }

    public static void setFloat(byte[] array, int offset, float value, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? FLOAT_LE : FLOAT_BE;
        handle.set(array, offset, value);
    }

    public static void setDouble(byte[] array, int offset, double value, ByteOrder order) {
        var handle = order == ByteOrder.LITTLE_ENDIAN ? DOUBLE_LE : DOUBLE_BE;
        handle.set(array, offset, value);
    }
}
