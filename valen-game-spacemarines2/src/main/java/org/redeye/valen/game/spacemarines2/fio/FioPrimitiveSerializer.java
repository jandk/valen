package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public interface FioPrimitiveSerializer<T> extends FioSerializer<T> {
    List<T> loadArray(DataSource source, int size) throws IOException;
}
