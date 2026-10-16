package be.twofold.valen.game.eternal.reader.soundcontainer;

import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record SoundEntry(
    long hash,
    MediaId mediaId,
    int sizeOnDisk,
    int offset,
    int size,
    SoundEntryType type,
    short headerSize,
    int headerOffset
) {
    public static SoundEntry read(BinarySource source) throws IOException {
        var hash = source.readLong();
        var mediaId = MediaId.of(source.readInt());
        var sizeOnDisk = source.readInt();
        var offset = source.readInt();
        var size = source.readInt();
        var type = SoundEntryType.read(source);
        var headerSize = source.readShort();
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
