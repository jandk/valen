package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioBoolSerializer extends FioPrimitiveSerializerImpl<Boolean> {
    @Override
    public Boolean load(DataSource source) throws IOException {
        return source.readBoolByte();
    }

    @Override
    public List<Boolean> loadArray(DataSource source, int count) throws IOException {
        return source.readStructs(count, DataSource::readBoolByte);
    }
}
