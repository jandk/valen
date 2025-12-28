package be.twofold.valen.game.colossus.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourceDiskHeader(
    long headerHash
) {
    public static ResourceDiskHeader read(BinarySource source) throws IOException {
        source.expectInt(0x4c434449); // magic
        source.expectInt(12); // version
        var headerHash = source.readLong();

        return new ResourceDiskHeader(headerHash);
    }
}
