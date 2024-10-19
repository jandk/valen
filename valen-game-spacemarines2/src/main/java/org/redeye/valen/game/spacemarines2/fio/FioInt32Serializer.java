package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioInt32Serializer extends FioPrimitiveSerializerImpl<Integer> {
    @Override
    public Integer load(DataSource source) throws IOException {
        return source.readInt();
    }

    @Override
    public List<Integer> loadArray(DataSource source, int count) throws IOException {
        return source.readStructs(count, DataSource::readInt);
    }
}
