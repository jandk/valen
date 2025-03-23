package be.twofold.valen.game.source.readers.vpk;

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
        var crc = source.readInt();
        var preloadBytesLength = source.readShort();
        var archiveIndex = source.readShort();
        var entryOffset = source.readInt();
        var entryLength = source.readInt();
        source.expectShort((short) 0xFFFF);

        byte[] preloadBytes;
        if (preloadBytesLength != 0) {
            preloadBytes = source.readBytes(preloadBytesLength);
        } else preloadBytes = EMPTY;

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
