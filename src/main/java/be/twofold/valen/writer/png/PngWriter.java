package be.twofold.valen.writer.png;

import java.io.*;
import java.nio.channels.*;

public final class PngWriter {
    private final WritableByteChannel channel;

    public PngWriter(WritableByteChannel channel) {
        this.channel = channel;
    }

    public void write(Png png) throws IOException {
        try (PngOutputStream output = new PngOutputStream(
            Channels.newOutputStream(channel), png.format())) {
            output.writeImage(png.data());
        }
    }
}
