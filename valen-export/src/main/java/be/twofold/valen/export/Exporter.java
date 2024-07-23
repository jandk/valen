package be.twofold.valen.export;

import java.io.*;

public interface Exporter<T> {

    String getExtension();

    Class<T> getSupportedType();

    void export(T value, OutputStream out) throws IOException;

}
