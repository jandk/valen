package be.twofold.valen.middleware.wwise.bnk;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record MediaHeader(
    int id,
    int offset,
    int size
) {
    public static final int BYTES = 12;

    public static MediaHeader read(BinarySource source) throws IOException {
        int id = source.readInt();
        int offset = source.readInt();
        int size = source.readInt();
        return new MediaHeader(id, offset, size);
    }
}
