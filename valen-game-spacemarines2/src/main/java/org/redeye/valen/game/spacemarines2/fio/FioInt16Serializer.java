package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioInt16Serializer extends FioPrimitiveSerializerImpl<Short> {
    public FioInt16Serializer(int flags) {
        super(flags);
    }

    public FioInt16Serializer() {
    }

    @Override
    public Short load(DataSource source) throws IOException {
        return source.readShort();
    }

    @Override
    public List<Short> loadArray(DataSource source, int count) throws IOException {
        short[] bytes = source.readShorts(count);
        List<Short> arr = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            arr.add(bytes[i]);
        }
        return arr;
    }
}
