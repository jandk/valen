package be.twofold.valen.core.io;

import java.io.*;

@FunctionalInterface
public interface StructMapper<T> {
    T read(DataSource source) throws IOException;
}
