package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioUUIDSerializer extends FioPrimitiveSerializerImpl<UUID> {
    public FioUUIDSerializer(int flags) {
        super(flags);
    }

    public FioUUIDSerializer() {
    }

    @Override
    public UUID load(DataSource source) throws IOException {
        return new UUID(source.readLong(), source.readLong());
    }

    @Override
    public List<UUID> loadArray(DataSource source, int count) throws IOException {
        List<UUID> arr = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            arr.add(new UUID(source.readLong(), source.readLong()));
        }
        return arr;
    }
}
