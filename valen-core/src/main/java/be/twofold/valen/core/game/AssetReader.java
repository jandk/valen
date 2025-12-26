package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(BinarySource source, A asset) throws IOException;

    default Class<?> getReturnType() {
        return Reflections.getParameterizedType(getClass(), AssetReader.class)
            .map(type -> (Class<?>) type.getActualTypeArguments()[0])
            .orElseThrow();
    }

    static <A extends Asset> AssetReader<Bytes, A> raw() {
        return new AssetReader<>() {
            @Override
            public boolean canRead(A asset) {
                return true;
            }

            @Override
            public Bytes read(BinarySource source, A asset) throws IOException {
                return source.readBytes(Math.toIntExact(source.size()));
            }
        };
    }
}
