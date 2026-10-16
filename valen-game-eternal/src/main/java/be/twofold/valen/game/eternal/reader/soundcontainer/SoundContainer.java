package be.twofold.valen.game.eternal.reader.soundcontainer;

import be.twofold.valen.middleware.wwise.wem.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record SoundContainer(
    Bytes headers,
    List<SoundEntry> entries
) {
    private static final WemReader WEM_READER = new WemReader();

    public static SoundContainer read(BinarySource source) throws IOException {
        source.expectInt(6); // version
        source.skip(4); // indexSize
        var headers = source.readBytes(source.readInt() - 4);
        var entries = source.readObjects(source.readInt(), SoundEntry::read);

        for (SoundEntry entry : entries) {
            var slice = headers.slice(entry.headerOffset(), entry.headerSize());
            WEM_READER.read(BinarySource.wrap(slice), null, null);
        }

        return new SoundContainer(
            headers,
            entries
        );
    }
}
