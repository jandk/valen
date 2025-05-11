package be.twofold.valen.core.game;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(DataSource source, A asset) throws IOException;

    default Class<?> getReturnType() {
        return Reflections.getParameterizedType(getClass(), AssetReader.class)
            .map(type -> (Class<?>) type.getActualTypeArguments()[0])
            .orElseThrow();
    }

    static <A extends Asset> AssetReader<ByteBuffer, A> raw() {
        return new AssetReader<>() {
            @Override
            public boolean canRead(A asset) {
                return true;
            }

            @Override
            public ByteBuffer read(DataSource source, A asset) throws IOException {
                return source.readBuffer(Math.toIntExact(source.size()));
            }
        };
    }
}
