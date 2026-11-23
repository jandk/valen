package be.twofold.valen.core.export;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class RawExporter implements Exporter<Bytes> {
    @Override
    public String getID() {
        return "binary.raw";
    }

    @Override
    public String getName() {
        return "Raw Bytes";
    }

    @Override
    public String getExtension() {
        return "";
    }

    @Override
    public Class<Bytes> getSupportedType() {
        return Bytes.class;
    }

    @Override
    public void export(Bytes value, BinaryWriter writer) throws IOException {
        writer.writeBytes(value);
    }
}
