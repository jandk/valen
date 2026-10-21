package be.twofold.valen.game.darkages.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Bank(
    String name,
    int id,
    boolean localized,
    String packageName
) {
    public static Bank read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var id = source.readInt();
        var localized = source.readBool(BoolFormat.BYTE);
        var packageName = source.readString(StringFormat.INT_LENGTH);

        return new Bank(
            name,
            id,
            localized,
            packageName
        );
    }
}
