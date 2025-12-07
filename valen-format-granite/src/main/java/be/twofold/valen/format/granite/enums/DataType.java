package be.twofold.valen.format.granite.enums;

import be.twofold.valen.core.util.*;

public enum DataType implements ValueEnum<Integer> {
    R8G8B8_SRGB(0),
    R8G8B8A8_SRGB(1),
    X8Y8Z0_TANGENT(2),
    R8G8B8_LINEAR(3),
    R8G8B8A8_LINEAR(4),
    X8(5),
    X8Y8(6),
    X8Y8Z8(7),
    X8Y8Z8W8(8),
    X16(9),
    X16Y16(10),
    X16Y16Z16(11),
    X16Y16Z16W16(12),
    X32(13),
    X32_FLOAT(14),
    X32Y32(15),
    X32Y32_FLOAT(16),
    X32Y32Z32(17),
    X32Y32Z32_FLOAT(18),
    R32G32B32(19),
    R32G32B32_FLOAT(20),
    X32Y32Z32W32(21),
    X32Y32Z32W32_FLOAT(22),
    R32G32B32A32(23),
    R32G32B32A32_FLOAT(24),
    R16G16B16_FLOAT(25),
    R16G16B16A16_FLOAT(26),
    ;

    private final int value;

    DataType(int value) {
        this.value = value;
    }

    public static DataType fromValue(int value) {
        return ValueEnum.fromValue(DataType.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
