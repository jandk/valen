package be.twofold.valen.game.eternal.reader.soundmetadata;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record StringPool(
    Bytes value,
    Ints indices
) {
    public static StringPool read(BinarySource source) throws IOException {
        var value = source.readBytes(source.readInt());
        var count = (int) value.stream().filter(b -> b == 0).count();

        var indices = new int[count + 1];

        for (int i = 0, o = 0; i < value.length(); i++) {
            if (value.get(i) == 0) {
                indices[o++] = i;
            }
        }
        indices[indices.length - 1] = value.length();
        return new StringPool(value, Ints.copyOf(indices));
    }
}
