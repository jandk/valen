package be.twofold.valen.game.idtech.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record NameId(
    String name,
    int id
) {
    public static NameId read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var id = source.readInt();

        return new NameId(
            name, id
        );
    }

    public static NameId readReversed(BinarySource source) throws IOException {
        var id = source.readInt();
        var name = source.readString(StringFormat.INT_LENGTH);

        return new NameId(
            name, id
        );
    }
}
