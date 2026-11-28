package be.twofold.valen.game.doom.readers;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class IdStrings {
    private IdStrings() {
    }

    public static String read(BinaryReader reader) throws IOException {
        assert reader.order() == ByteOrder.BIG_ENDIAN;
        var length = Integer.reverseBytes(reader.readInt());
        return reader.readString(length);
    }

    public static List<String> read(BinaryReader reader, int count) throws IOException {
        assert reader.order() == ByteOrder.BIG_ENDIAN;
        var result = new ArrayList<String>(count);
        for (int i = 0; i < count; i++) {
            result.add(read(reader));
        }
        return List.copyOf(result);
    }
}
