package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;

@FunctionalInterface
public interface GeoReader<T extends Buffer> {
    void read(DataSource source, T buffer) throws IOException;
}
