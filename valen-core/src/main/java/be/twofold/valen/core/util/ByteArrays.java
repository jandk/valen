package be.twofold.valen.core.util;

import java.lang.invoke.*;
import java.nio.*;

public final class ByteArrays {
    private static final VarHandle SHORT_VH_LE =
        MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle INT_VH_LE =
        MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_VH_LE =
        MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle FLOAT_VH_LE =
        MethodHandles.byteArrayViewVarHandle(float[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle DOUBLE_VH_LE =
        MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN);

    private ByteArrays() {
    }

    public static short getShort(byte[] bytes, int offset) {
        return (short) SHORT_VH_LE.get(bytes, offset);
    }

    public static int getInt(byte[] bytes, int offset) {
        return (int) INT_VH_LE.get(bytes, offset);
    }

    public static long getLong(byte[] bytes, int offset) {
        return (long) LONG_VH_LE.get(bytes, offset);
    }

    public static float getFloat(byte[] bytes, int offset) {
        return (float) FLOAT_VH_LE.get(bytes, offset);
    }

    public static double getDouble(byte[] bytes, int offset) {
        return (double) DOUBLE_VH_LE.get(bytes, offset);
    }


    public static void setShort(byte[] bytes, int offset, short value) {
        SHORT_VH_LE.set(bytes, offset, value);
    }

    public static void setInt(byte[] bytes, int offset, int value) {
        INT_VH_LE.set(bytes, offset, value);
    }

    public static void setLong(byte[] bytes, int offset, long value) {
        LONG_VH_LE.set(bytes, offset, value);
    }

    public static void setFloat(byte[] bytes, int offset, float value) {
        FLOAT_VH_LE.set(bytes, offset, value);
    }

    public static void setDouble(byte[] bytes, int offset, double value) {
        DOUBLE_VH_LE.set(bytes, offset, value);
    }
}
