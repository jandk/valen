package be.twofold.valen.game.doom.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public record ResourcesIndexHeader(
    int size,
    int count
) {
    public static ResourcesIndexHeader read(BinarySource source) throws IOException {
        source.expectInt(0x5245_5305);
        source.order(ByteOrder.BIG_ENDIAN);
        var size = source.readInt();
        for (var i = 0; i < 6; i++) {
            source.expectInt(0);
        }
        var count = source.readInt();

        return new ResourcesIndexHeader(
            size,
            count
        );
    }
}
