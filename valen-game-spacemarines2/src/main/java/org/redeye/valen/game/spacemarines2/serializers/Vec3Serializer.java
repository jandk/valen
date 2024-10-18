package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.fio.*;

import java.io.*;
import java.util.*;

public class Vec3Serializer extends FioPrimitiveSerializerImpl<Vector3> {
    @Override
    public Vector3 load(DataSource source) throws IOException {
        return Vector3.read(source);
    }

    @Override
    public int flags() {
        return 16;
    }

    @Override
    public List<Vector3> loadArray(DataSource source, int size) throws IOException {
        List<Vector3> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            data.add(Vector3.read(source));
        }
        return data;
    }
}
