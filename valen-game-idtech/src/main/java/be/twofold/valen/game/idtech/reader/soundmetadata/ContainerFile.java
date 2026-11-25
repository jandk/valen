package be.twofold.valen.game.idtech.reader.soundmetadata;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ContainerFile(
    int id,
    Ints mask
) {
    public static ContainerFile read(BinaryReader reader) throws IOException {
        var id = reader.readInt();
        var mask = reader.readInts(reader.readInt());

        return new ContainerFile(
            id,
            mask
        );
    }
}
