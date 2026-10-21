package be.twofold.valen.game.idtech.reader.soundcontainer;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record SoundEntry(
    long hash,
    int mediaId,
    int sizeOnDisk,
    int offset,
    int size,
    SoundEntryType type,
    int headerSize,
    int headerOffset
) {
    public static SoundEntry read(BinarySource source, SoundFormat format) throws IOException {
        var hash = source.readLong();
        var mediaId = source.readInt();
        var sizeOnDisk = source.readInt();
        var offset = source.readInt();
        var size = source.readInt();

        SoundEntryType type;
        int headerSize;
        switch (format) {
            case ETERNAL -> {
                type = SoundEntryType.read(source);
                headerSize = Short.toUnsignedInt(source.readShort());
            }
            case DARK_AGES -> {
                type = SoundEntryType.NONE;
                headerSize = source.readInt();
            }
            default -> throw new UnsupportedOperationException("Unsupported sound format: " + format);
        }

        var headerOffset = source.readInt();

        return new SoundEntry(
            hash,
            mediaId,
            sizeOnDisk,
            offset,
            size,
            type,
            headerSize,
            headerOffset
        );
    }
}
