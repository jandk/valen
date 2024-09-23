package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioInt64Serializer extends FioPrimitiveSerializerImpl<Long> {
    public FioInt64Serializer(int flags) {
        super(flags);
    }

    public FioInt64Serializer() {
    }

    @Override
    public Long load(DataSource source) throws IOException {
        return source.readLong();
    }

    @Override
    public List<Long> loadArray(DataSource source, int count) throws IOException {
        long[] bytes = source.readLongs(count);
        List<Long> arr = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            arr.add(bytes[i]);
        }
        return arr;
    }
}
