package be.twofold.valen.game.doom.readers.staticmodel;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.time.*;

public record StaticModelHeader(
    byte version,
    Instant sourceTimeStamp,
    int numSurfaces
) {
    public static StaticModelHeader read(BinaryReader reader) throws IOException {
        var magic = reader.readInt();
        if ((magic & 0xFFFFFF) != StaticModel.MAGIC) {
            throw new IOException("Invalid magic number");
        }

        var version = (byte) (magic >>> 24);
        if (version < 26 || version > 27) {
            throw new IOException("Invalid version");
        }

        var sourceTimeStamp = Instant.ofEpochSecond(reader.readInt());
        var numSurfaces = reader.readInt();

        return new StaticModelHeader(
            version,
            sourceTimeStamp,
            numSurfaces
        );
    }
}
