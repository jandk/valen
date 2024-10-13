package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioBoolSerializer extends FioPrimitiveSerializerImpl<Boolean> {
    public FioBoolSerializer(int flags) {
        super(flags);
    }

    public FioBoolSerializer() {
        this(16);
    }

    @Override
    public Boolean load(DataSource source) throws IOException {
        return source.readByte() == 1;
    }

    @Override
    public List<Boolean> loadArray(DataSource source, int count) throws IOException {
        byte[] bytes = source.readBytes(count);
        List<Boolean> arr = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            arr.add(bytes[i] == 1);
        }
        return arr;
    }
}
