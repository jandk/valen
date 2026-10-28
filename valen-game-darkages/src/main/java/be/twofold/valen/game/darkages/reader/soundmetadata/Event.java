package be.twofold.valen.game.darkages.reader.soundmetadata;

import be.twofold.valen.game.idtech.reader.soundmetadata.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Event(
    String name,
    int id,
    float maxAttenuation,
    boolean nonLocalized,
    boolean looping,
    float duration,
    List<LanguageDuration> languageDurations,
    List<NameId> mediaIds
) {
    public static Event read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var id = source.readInt();
        var maxAttenuation = source.readFloat();
        source.expectByte((byte) 0); // unused
        var nonLocalized = source.readBool(BoolFormat.BYTE);
        var looping = source.readBool(BoolFormat.BYTE);
        var duration = source.readFloat();
        var languageDurations = nonLocalized
            ? List.<LanguageDuration>of()
            : source.readObjects(source.readInt(), LanguageDuration::read);
        var mediaIds = source.readObjects(source.readInt(), NameId::readReversed);

        return new Event(
            name,
            id,
            maxAttenuation,
            nonLocalized,
            looping,
            duration,
            languageDurations,
            mediaIds
        );
    }
}
