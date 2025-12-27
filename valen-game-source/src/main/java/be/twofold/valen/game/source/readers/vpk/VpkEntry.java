package be.twofold.valen.game.source.readers.vpk;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record VpkEntry(
    String name,
    int crc,
    Bytes preloadBytes,
    short archiveIndex,
    int entryOffset,
    int entryLength
) {
    public static VpkEntry read(BinarySource source, String extension, String directory, String filename) throws IOException {
        var crc = source.readInt();
        var preloadBytesLength = source.readShort();
        var archiveIndex = source.readShort();
        var entryOffset = source.readInt();
        var entryLength = source.readInt();
        source.expectShort((short) 0xFFFF);

        var preloadBytes = preloadBytesLength != 0
            ? source.readBytes(preloadBytesLength)
            : Bytes.empty();

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
