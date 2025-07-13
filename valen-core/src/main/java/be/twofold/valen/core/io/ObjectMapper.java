package be.twofold.valen.core.io;

import java.io.*;

@FunctionalInterface
public interface ObjectMapper<T> {
    T read(BinaryReader reader) throws IOException;
}
