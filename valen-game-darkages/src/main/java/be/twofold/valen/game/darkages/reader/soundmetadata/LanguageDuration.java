package be.twofold.valen.game.darkages.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record LanguageDuration(
    String language,
    float duration
) {
    public static LanguageDuration read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.INT_LENGTH);
        var duration = reader.readFloat();

        return new LanguageDuration(
            name,
            duration
        );
    }
}
