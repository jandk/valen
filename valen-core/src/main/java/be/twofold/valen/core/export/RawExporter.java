package be.twofold.valen.core.export;

import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.channels.*;

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
    public void export(Bytes value, OutputStream out) throws IOException {
        Channels.newChannel(out).write(value.asBuffer());
    }
}
