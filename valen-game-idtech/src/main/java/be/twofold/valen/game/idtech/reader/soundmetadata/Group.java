package be.twofold.valen.game.idtech.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Group(
    int id,
    String name,
    List<NameId> values
) {
    public static Group read(BinaryReader reader) throws IOException {
        int id = reader.readInt();
        var name = reader.readString(StringFormat.INT_LENGTH);
        var values = reader.readObjects(reader.readInt(), NameId::readReversed);

        return new Group(
            id,
            name,
            values
        );
    }
}
