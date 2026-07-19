package be.twofold.valen.game.qc.reader.pak;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record PakFooter(
    Bytes key
) {
    public static final int BYTES = 40;

    public static PakFooter read(BinarySource source) throws IOException {
        source.position(source.size() - BYTES);

        Bytes key = source.readBytes(32);
        source.expectLong(0x3153_4B06_4406_5031L);

        return new PakFooter(key);
    }

    @Override
    public String toString() {
        return "PakFooter[" +
            "key=" + key.toHexString(HexFormat.of()) +
            "]";
    }
}
