package be.twofold.valen.middleware.wwise.codebook;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record CodebookLibrary(
    String name,
    Shorts indices
) {
    public static CodebookLibrary read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.SHORT_LENGTH);
        var count = Short.toUnsignedInt(source.readShort());
        var indices = source.readShorts(count);

        return new CodebookLibrary(
            name,
            indices
        );
    }
}
