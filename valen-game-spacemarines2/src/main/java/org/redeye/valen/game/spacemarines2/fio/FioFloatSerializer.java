package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioFloatSerializer extends FioPrimitiveSerializerImpl<Float> {
    public FioFloatSerializer(int flags) {
        super(flags);
    }

    public FioFloatSerializer() {
    }

    @Override
    public Float load(DataSource source) throws IOException {
        return source.readFloat();
    }

    @Override
    public List<Float> loadArray(DataSource source, int count) throws IOException {
        float[] bytes = source.readFloats(count);
        List<Float> arr = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            arr.add(bytes[i]);
        }
        return arr;
    }
}
