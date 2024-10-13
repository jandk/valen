package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.fio.*;

import java.io.*;
import java.util.*;

public class IntVec4Serializer extends FioPrimitiveSerializerImpl<Vector4> {
    @Override
    public Vector4 load(DataSource source) throws IOException {
        return new Vector4(source.readInt(), source.readInt(), source.readInt(), source.readInt());
    }

    @Override
    public int flags() {
        return 16;
    }

    @Override
    public List<Vector4> loadArray(DataSource source, int size) throws IOException {
        List<Vector4> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            data.add(new Vector4(source.readInt(), source.readInt(), source.readInt(), source.readInt()));
        }
        return data;
    }
}
