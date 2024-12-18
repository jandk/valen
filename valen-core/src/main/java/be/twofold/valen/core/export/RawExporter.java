package be.twofold.valen.core.export;

import java.io.*;

public final class RawExporter implements Exporter<byte[]> {
    @Override
    public String getExtension() {
        return "";
    }

    @Override
    public Class<byte[]> getSupportedType() {
        return byte[].class;
    }

    @Override
    public void export(byte[] value, OutputStream out) throws IOException {
        out.write(value);
    }
}
