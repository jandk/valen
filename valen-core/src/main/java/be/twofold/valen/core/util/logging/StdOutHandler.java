package be.twofold.valen.core.util.logging;

import java.io.*;
import java.util.logging.*;

public final class StdOutHandler extends StreamHandler {
    public StdOutHandler() {
        super(nonClosing(System.out), new ColoredFormatter());
    }

    @Override
    public synchronized void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    private static OutputStream nonClosing(OutputStream out) {
        return new FilterOutputStream(out) {
            @Override
            public void close() throws IOException {
                flush();
            }
        };
    }
}
