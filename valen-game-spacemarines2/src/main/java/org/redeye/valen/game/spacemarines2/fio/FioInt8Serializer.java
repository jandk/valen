package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioInt8Serializer extends FioPrimitiveSerializerImpl<Byte> {
    @Override
    public Byte load(DataSource source) throws IOException {
        return source.readByte();
    }

    @Override
    public List<Byte> loadArray(DataSource source, int count) throws IOException {
        return source.readObjects(count, DataSource::readByte);
    }
}
