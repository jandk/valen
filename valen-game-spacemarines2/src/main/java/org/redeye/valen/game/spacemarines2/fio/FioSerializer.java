package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;

public interface FioSerializer<T> {
    T load(DataSource source) throws IOException;

    int flags();
}
