package be.twofold.valen.writer.dds;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public final class DdsWriter {
    private final WritableByteChannel channel;

    public DdsWriter(WritableByteChannel channel) {
        this.channel = channel;
    }

    public void write(Dds dds) throws IOException {
        channel.write(DdsHeader.create(dds.info()).toBuffer());
        for (byte[] mipMap : dds.mipMaps()) {
            channel.write(ByteBuffer.wrap(mipMap));
        }
    }
}
