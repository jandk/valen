package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;

public enum GdexItemType implements ValueEnum<Integer> {
    RAW(0),
    STRUCT(1),
    STRING(2),
    INT32(3),
    INT64(4),
    FLOAT(5),
    DOUBLE(6),
    DATE(7),
    INT32_ARRAY(8),
    FLOAT_ARRAY(9),
    INT64_ARRAY(10),
    DOUBLE_ARRAY(11),
    GUID(12),
    GUID_ARRAY(13),
    ;

    private final int value;

    GdexItemType(int value) {
        this.value = value;
    }

    public static GdexItemType read(BinaryReader reader) throws IOException {
        return ValueEnum.fromValue(GdexItemType.class, (int) reader.readByte());
    }

    @Override
    public Integer value() {
        return value;
    }
}
