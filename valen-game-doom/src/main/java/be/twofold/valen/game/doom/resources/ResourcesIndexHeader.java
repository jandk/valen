package be.twofold.valen.game.doom.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public record ResourcesIndexHeader(
    int size,
    int count
) {
    public static ResourcesIndexHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x5245_5305);
        reader.order(ByteOrder.BIG_ENDIAN);
        var size = reader.readInt();
        for (var i = 0; i < 6; i++) {
            reader.expectInt(0);
        }
        var count = reader.readInt();

        return new ResourcesIndexHeader(
            size,
            count
        );
    }
}
