package be.twofold.valen.game.darkages.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Bank(
    String name,
    int id,
    boolean localized,
    String packageName
) {
    public static Bank read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.INT_LENGTH);
        var id = reader.readInt();
        var localized = reader.readBool(BoolFormat.BYTE);
        var packageName = reader.readString(StringFormat.INT_LENGTH);

        return new Bank(
            name,
            id,
            localized,
            packageName
        );
    }
}
