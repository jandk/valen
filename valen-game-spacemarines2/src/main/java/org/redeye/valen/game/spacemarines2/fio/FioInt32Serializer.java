package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioInt32Serializer extends FioPrimitiveSerializerImpl<Integer> {
    public FioInt32Serializer(int flags) {
        super(flags);
    }

    public FioInt32Serializer() {
        this(16);
    }

    @Override
    public Integer load(DataSource source) throws IOException {
        return source.readInt();
    }

    @Override
    public List<Integer> loadArray(DataSource source, int count) throws IOException {
        int[] bytes = source.readInts(count);
        List<Integer> arr = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            arr.add(bytes[i]);
        }
        return arr;
    }
}
