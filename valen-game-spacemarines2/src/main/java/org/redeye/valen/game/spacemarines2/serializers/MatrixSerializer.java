package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.fio.*;

import java.io.*;
import java.util.*;

public class MatrixSerializer extends FioPrimitiveSerializerImpl<Matrix4> {
    public MatrixSerializer(int flags) {
        super(flags);
    }

    public MatrixSerializer() {
    }

    @Override
    public Matrix4 load(DataSource source) throws IOException {
        return Matrix4.fromArray(source.readFloats(16));
    }


    @Override
    public List<Matrix4> loadArray(DataSource source, int size) throws IOException {
        List<Matrix4> matrices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            matrices.add(Matrix4.fromArray(source.readFloats(16)));
        }
        return matrices;
    }
}
