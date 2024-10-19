package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public class FioQuatSerializer extends FioPrimitiveSerializerImpl<Quaternion> {
    @Override
    public Quaternion load(DataSource source) throws IOException {
        return Quaternion.read(source);
    }

    @Override
    public List<Quaternion> loadArray(DataSource source, int size) throws IOException {
        return source.readStructs(size, Quaternion::read);
    }
}
