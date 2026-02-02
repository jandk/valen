package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(BinarySource source, A asset, LoadingContext context) throws IOException;

    default Class<?> getReturnType() {
        return Reflections.getParameterizedType(getClass(), AssetReader.class)
            .map(type -> (Class<?>) type.getActualTypeArguments()[0])
            .orElseThrow();
    }

}
