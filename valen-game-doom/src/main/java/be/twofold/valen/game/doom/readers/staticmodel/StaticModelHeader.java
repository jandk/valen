package be.twofold.valen.game.doom.readers.staticmodel;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.time.*;

public record StaticModelHeader(
    Instant sourceTimeStamp,
    int numSurfaces
) {
    public static StaticModelHeader read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);

        reader.expectInt(StaticModel.MAGIC);
        var sourceTimeStamp = Instant.ofEpochSecond(reader.readInt());
        var numSurfaces = reader.readInt();

        return new StaticModelHeader(
            sourceTimeStamp,
            numSurfaces
        );
    }
}
