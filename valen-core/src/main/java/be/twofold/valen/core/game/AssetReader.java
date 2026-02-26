package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.reflect.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(A asset, LoadingContext context) throws IOException;

    default Class<?> getReturnType() {
        return getReturnType(getClass());
    }

    private Class<?> getReturnType(Class<?> clazz) {
        for (var genericInterface : clazz.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType().equals(AssetReader.class)) {
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
            try (BinarySource source = BinarySource.wrap(context.open(asset.location()))) {
                return read(source, asset, context);
            }
        }

        R read(BinarySource source, A asset, LoadingContext context) throws IOException;
    }

    final class Raw implements Binary<Bytes, Asset> {
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
