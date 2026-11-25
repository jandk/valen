package be.twofold.valen.game.darkages.reader.resources;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record ResourcesStrings(
    long numStrings,
    Longs offsets,
    List<String> values
) {
    public static ResourcesStrings read(BinaryReader reader) throws IOException {
        var numStrings = reader.readLong();
        var offsets = reader.readLongs(Math.toIntExact(numStrings));
        var values = reader.readObjects(Math.toIntExact(numStrings), s -> s.readString(StringFormat.NULL_TERM));

        return new ResourcesStrings(
            numStrings,
            offsets,
            values
        );
    }
}
