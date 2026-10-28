package be.twofold.valen.game.idtech.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Group(
    int id,
    String name,
    List<NameId> values
) {
    public static Group read(BinarySource source) throws IOException {
        int id = source.readInt();
        var name = source.readString(StringFormat.INT_LENGTH);
        var values = source.readObjects(source.readInt(), NameId::readReversed);

        return new Group(
            id,
            name,
            values
        );
    }
}
