package be.twofold.valen.game.colossus.reader.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourceDiskHeader(
    int magic,
    int version,
    long headerHash
) {
    public static ResourceDiskHeader read(DataSource source) throws IOException {
        var magic = source.readInt();
        var version = source.readInt();
        var headerHash = source.readLong();

        if (magic != 0x4c434449) {
            throw new IOException("Invalid magic: " + magic);
        }
        if (version != 12) {
            throw new IOException("Invalid version: " + version);
        }

        return new ResourceDiskHeader(
            magic,
            version,
            headerHash
        );
    }
}
