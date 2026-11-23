package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(A asset, LoadingContext context) throws IOException;

    default Optional<Meta.Node> readMetadata(A asset, LoadingContext context) throws IOException {
        return Optional.empty();
    }

    default Class<?> getReturnType() {
        return getReturnType(getClass());
    }

    private Class<?> getReturnType(Class<?> clazz) {
        for (var genericInterface : clazz.getGenericInterfaces()) {
            if (!(genericInterface instanceof ParameterizedType parameterizedType)) {
                continue;
            }
            if (parameterizedType.getRawType().equals(AssetReader.class) ||
                parameterizedType.getRawType().equals(AssetReader.Binary.class)) {
                return (Class<?>) parameterizedType.getActualTypeArguments()[0];
            }
        }

        var superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            return getReturnType(superclass);
        }

        throw new UnsupportedOperationException(
            clazz.getSimpleName() + " does not implement AssetReader with proper type parameters");
    }

    interface Binary<R, A extends Asset> extends AssetReader<R, A> {
        @Override
        default R read(A asset, LoadingContext context) throws IOException {
            try (BinaryReader reader = BinaryReader.wrap(context.open(asset.location()))) {
                return read(reader, asset, context);
            }
        }

        R read(BinaryReader reader, A asset, LoadingContext context) throws IOException;
    }

    final class Raw implements Binary<Bytes, Asset> {
        @Override
        public boolean canRead(Asset asset) {
            return true;
        }

        @Override
        public Bytes read(Asset asset, LoadingContext context) throws IOException {
            return context.open(asset.location());
        }

        @Override
        public Bytes read(BinaryReader reader, Asset asset, LoadingContext context) throws IOException {
            return reader.readRemaining();
        }
    }

}
