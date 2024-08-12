package be.twofold.valen.reader;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.resource.*;

import java.io.*;
import java.lang.reflect.*;

public interface ResourceReader<R> {

    boolean canRead(ResourceKey key);

    R read(DataSource source, Asset<ResourceKey> asset) throws IOException;

    default Class<?> getReadType() {
        var genericInterfaces = getClass().getGenericInterfaces();
        var parameterizedType = ((ParameterizedType) genericInterfaces[0]);
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }

}
