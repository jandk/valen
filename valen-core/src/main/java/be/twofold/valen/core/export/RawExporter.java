package be.twofold.valen.core.export;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public final class RawExporter implements Exporter<ByteBuffer> {
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
    public Class<ByteBuffer> getSupportedType() {
        return ByteBuffer.class;
    }

    @Override
    public void export(ByteBuffer value, OutputStream out) throws IOException {
        Channels.newChannel(out).write(value);
    }
}
