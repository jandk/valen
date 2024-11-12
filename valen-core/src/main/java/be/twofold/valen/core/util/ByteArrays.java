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

    public static short readShort(byte[] bytes, int offset) {
        return (short) SHORT_VH_LE.get(bytes, offset);
    }

    public static int readInt(byte[] bytes, int offset) {
        return (int) INT_VH_LE.get(bytes, offset);
    }

    public static long readLong(byte[] bytes, int offset) {
        return (long) LONG_VH_LE.get(bytes, offset);
    }

    public static float readFloat(byte[] bytes, int offset) {
        return (float) FLOAT_VH_LE.get(bytes, offset);
    }

    public static double readDouble(byte[] bytes, int offset) {
        return (double) DOUBLE_VH_LE.get(bytes, offset);
    }
}
