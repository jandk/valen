package org.redeye.valen.game.source1.vpk;

import be.twofold.valen.core.io.*;

import java.io.*;

public record VpkEntry(
    String name,
    int crc,
    byte[] preloadBytes,
    short archiveIndex,
    int entryOffset,
    int entryLength
) {
    private static final byte[] EMPTY = new byte[0];

    public static VpkEntry read(DataSource source, String extension, String directory, String filename) throws IOException {
        int crc = source.readInt();
        short preloadBytesLength = source.readShort();
        byte[] preloadBytes = preloadBytesLength != 0
            ? source.readBytes(preloadBytesLength)
            : EMPTY;
        short archiveIndex = source.readShort();
        int entryOffset = source.readInt();
        int entryLength = source.readInt();
        source.expectShort((short) 0xFFFF);

        var fullName = new StringBuilder();
        if (!directory.isBlank()) {
            fullName.append(directory).append('/');
        }
        fullName.append(filename);
        if (!extension.isBlank()) {
            fullName.append('.').append(extension);
        }

        return new VpkEntry(
            fullName.toString(),
            crc,
            preloadBytes,
            archiveIndex,
            entryOffset,
            entryLength
        );
    }
}
