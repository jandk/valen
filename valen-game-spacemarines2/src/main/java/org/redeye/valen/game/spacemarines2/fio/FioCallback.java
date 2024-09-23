package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;

@FunctionalInterface
public interface FioCallback<T> {
    void call(T inst, DataSource source, FioStructSerializer<T> serializer) throws IOException;
}
