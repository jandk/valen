package be.twofold.valen.game.darkages.reader.mask;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record ContainerMask(
    Map<Long, Bytes> masks
) {
    public static ContainerMask read(BinarySource source) throws IOException {
        var count = source.readInt();

        var masks = new HashMap<Long, Bytes>();
        for (int i = 0; i < count; i++) {
            var key = source.readLong();
            var length = source.readInt();
            var value = source.readBytes(length * 8);
            masks.put(key, value);
        }
        return new ContainerMask(masks);
    }
}
