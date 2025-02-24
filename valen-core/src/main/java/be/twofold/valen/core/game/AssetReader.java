package be.twofold.valen.core.game;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.util.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(DataSource source, A asset) throws IOException;

    default Class<?> getReturnType() {
        return Arrays.stream(getClass().getGenericInterfaces())
            .filter(ParameterizedType.class::isInstance)
            .map(ParameterizedType.class::cast)
            .filter(type -> type.getRawType() == AssetReader.class)
            .map(type -> (Class<?>) type.getActualTypeArguments()[0])
            .findFirst().orElseThrow();
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
