package be.twofold.valen.game.idtech.reader.soundcontainer;

import be.twofold.valen.game.idtech.game.*;
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
    public static SoundEntry read(BinaryReader reader, IdTechGame game) throws IOException {
        var hash = reader.readLong();
        var mediaId = reader.readInt();
        var sizeOnDisk = reader.readInt();
        var offset = reader.readInt();
        var size = reader.readInt();

        SoundEntryType type;
        int headerSize;
        switch (game) {
            case ETERNAL -> {
                type = SoundEntryType.read(reader);
                headerSize = reader.readUnsignedShort();
            }
            case DARK_AGES -> {
                type = SoundEntryType.NONE;
                headerSize = reader.readInt();
            }
            default -> throw new UnsupportedOperationException("Unsupported sound format for " + game);
        }

        var headerOffset = reader.readInt();

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
