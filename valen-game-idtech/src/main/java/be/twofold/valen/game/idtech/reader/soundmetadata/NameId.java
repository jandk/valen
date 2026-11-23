package be.twofold.valen.game.idtech.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record NameId(
    String name,
    int id
) {
    public static NameId read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.INT_LENGTH);
        var id = reader.readInt();

        return new NameId(
            name, id
        );
    }

    public static NameId readReversed(BinaryReader reader) throws IOException {
        var id = reader.readInt();
        var name = reader.readString(StringFormat.INT_LENGTH);

        return new NameId(
            name, id
        );
    }
}
