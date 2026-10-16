package be.twofold.valen.game.eternal.reader.soundcontainer;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record SoundEntry(
    long hash,
    int mediaId,
    int sizeOnDisk,
    int dataOffset,
    int dataSize,
    SoundEntryType type,
    short headerSize,
    int headerOffset
) {
    public static SoundEntry read(BinarySource source) throws IOException {
        var hash = source.readLong();
        var mediaId = source.readInt();
        var sizeOnDisk = source.readInt();
        var dataOffset = source.readInt();
        var dataSize = source.readInt();
        var type = SoundEntryType.read(source);
        var headerSize = source.readShort();
        var headerOffset = source.readInt();

        return new SoundEntry(
            hash,
            mediaId,
            sizeOnDisk,
            dataOffset,
            dataSize,
            type,
            headerSize,
            headerOffset
        );
    }
}
