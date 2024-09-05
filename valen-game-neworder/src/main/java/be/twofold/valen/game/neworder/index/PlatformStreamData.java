package be.twofold.valen.game.neworder.index;

import be.twofold.valen.core.io.*;

import java.io.*;

public record PlatformStreamData(
    String lang,
    int streamOffset,
    int streamLength
) {
    public static PlatformStreamData read(DataSource source) throws IOException {
        var lang = source.readString(16).trim();
        var streamOffset = source.readIntBE();
        var streamLength = source.readIntBE();
        return new PlatformStreamData(lang, streamOffset, streamLength);
    }
}
