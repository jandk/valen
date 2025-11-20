package be.twofold.valen.export.dds;

import java.io.*;

public final class DdsException extends IOException {
    public DdsException(String message) {
        super(message);
    }

    public DdsException(String message, Throwable cause) {
        super(message, cause);
    }
}
