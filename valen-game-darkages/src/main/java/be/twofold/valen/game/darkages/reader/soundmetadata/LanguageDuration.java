package be.twofold.valen.game.darkages.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record LanguageDuration(
    String language,
    float duration
) {
    public static LanguageDuration read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var duration = source.readFloat();

        return new LanguageDuration(
            name,
            duration
        );
    }
}
