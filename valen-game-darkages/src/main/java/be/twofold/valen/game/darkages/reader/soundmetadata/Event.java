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
    public static Event read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.INT_LENGTH);
        var id = reader.readInt();
        var maxAttenuation = reader.readFloat();
        reader.expectByte((byte) 0); // unused
        var nonLocalized = reader.readBool(BoolFormat.BYTE);
        var looping = reader.readBool(BoolFormat.BYTE);
        var duration = reader.readFloat();
        var languageDurations = nonLocalized
            ? List.<LanguageDuration>of()
            : reader.readObjects(reader.readInt(), LanguageDuration::read);
        var mediaIds = reader.readObjects(reader.readInt(), NameId::readReversed);

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
