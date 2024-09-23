package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioInt8Serializer extends FioPrimitiveSerializerImpl<Byte> {
    public FioInt8Serializer(int flags) {
        super(flags);
    }

    public FioInt8Serializer() {
    }

    @Override
    public Byte load(DataSource source) throws IOException {
        return source.readByte();
    }

    @Override
    public List<Byte> loadArray(DataSource source, int count) throws IOException {
        byte[] bytes = source.readBytes(count);
        List<Byte> arr = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            arr.add(bytes[i]);
        }
        return arr;
    }
}
