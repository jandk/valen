package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.reflect.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(BinarySource source, A asset, LoadingContext context) throws IOException;

    default Class<?> getReturnType() {
        for (var genericInterface : getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType().equals(AssetReader.class)) {
                return (Class<?>) parameterizedType.getActualTypeArguments()[0];
            }
        }
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not implement AssetReader");
    }

    final class Raw implements AssetReader<Bytes, Asset> {
        @Override
        public boolean canRead(Asset asset) {
            return true;
        }

        @Override
        public Bytes read(BinarySource source, Asset asset, LoadingContext context) throws IOException {
            return source.readBytes(Math.toIntExact(source.remaining()));
        }
    }

}
