package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioFloatSerializer extends FioPrimitiveSerializerImpl<Float> {
    @Override
    public Float load(DataSource source) throws IOException {
        return source.readFloat();
    }

    @Override
    public List<Float> loadArray(DataSource source, int count) throws IOException {
        return source.readStructs(count, DataSource::readFloat);
    }
}
