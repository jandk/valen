package be.twofold.valen.game.idtech.reader.soundmetadata;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ContainerFile(
    int id,
    Ints mask
) {
    public static ContainerFile read(BinarySource source) throws IOException {
        var id = source.readInt();
        var mask = source.readInts(source.readInt());

        return new ContainerFile(
            id,
            mask
        );
    }
}
