package be.twofold.valen.middleware.wwise.codebook;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record CodebookLibrary(
    String name,
    Shorts indices
) {
    public static CodebookLibrary read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.SHORT_LENGTH);
        var count = reader.readUnsignedShort();
        var indices = reader.readShorts(count);

        return new CodebookLibrary(
            name,
            indices
        );
    }
}
