package be.twofold.valen.middleware.wwise.bnk;

import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record MediaHeader(
    MediaId id,
    int offset,
    int size
) {
    public static final int BYTES = 12;

    public static MediaHeader read(BinaryReader reader) throws IOException {
        MediaId id = MediaId.of(reader.readInt());
        int offset = reader.readInt();
        int size = reader.readInt();
        return new MediaHeader(id, offset, size);
    }
}
