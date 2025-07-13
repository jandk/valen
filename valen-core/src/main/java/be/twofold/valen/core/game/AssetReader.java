package be.twofold.valen.core.game;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(BinaryReader reader, A asset) throws IOException;

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
            public ByteBuffer read(BinaryReader reader, A asset) throws IOException {
                return reader.readBuffer(Math.toIntExact(reader.size()));
            }
        };
    }
}
