package be.twofold.valen.core.compression.jna;

import com.sun.jna.*;

public interface NativeEnum {
    static TypeConverter converter() {
        return new EnumTypeMapper();
    }

    int nativeValue();

    final class EnumTypeMapper implements TypeConverter {
        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            Class<?> targetType = context.getTargetType();
            if (!targetType.isEnum() || !NativeEnum.class.isAssignableFrom(targetType)) {
                throw new IllegalArgumentException("Target type is not an enum or does not implement TypedEnum: " + targetType);
            }
            for (Object enumConstant : targetType.getEnumConstants()) {
                if (((NativeEnum) enumConstant).nativeValue() == (int) nativeValue) {
                    return enumConstant;
                }
            }
            throw new IllegalArgumentException("No enum constant found in " + targetType + " for native value " + nativeValue);
        }

        @Override
        public Object toNative(Object value, ToNativeContext context) {
            if (!(value instanceof NativeEnum nativeEnum)) {
                throw new IllegalArgumentException("Value does not implement TypedEnum: " + value);
            }
            return nativeEnum.nativeValue();
        }

        @Override
        public Class<?> nativeType() {
            return Integer.class;
        }
    }
}
