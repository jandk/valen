package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioInt16Serializer extends FioPrimitiveSerializerImpl<Short> {

    @Override
    public Short load(DataSource source) throws IOException {
        return source.readShort();
    }

    @Override
    public List<Short> loadArray(DataSource source, int count) throws IOException {
        return source.readObjects(count, DataSource::readShort);
    }
}
