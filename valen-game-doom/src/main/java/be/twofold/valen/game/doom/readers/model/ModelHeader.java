package be.twofold.valen.game.doom.readers.model;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.time.*;

public record ModelHeader(
    Instant sourceTimeStamp,
    int numSurfaces
) {
    public static ModelHeader read(BinarySource source) throws IOException {
        source.order(ByteOrder.BIG_ENDIAN);

        source.expectInt(StaticModel.MAGIC);
        var sourceTimeStamp = Instant.ofEpochSecond(source.readInt());
        var numSurfaces = source.readInt();

        return new ModelHeader(
            sourceTimeStamp,
            numSurfaces
        );
    }
}
