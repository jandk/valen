package be.twofold.valen.game.idtech.reader.soundcontainer;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record SoundContainer(
    Bytes headers,
    List<SoundEntry> entries
) {
    public static SoundContainer read(BinarySource source) throws IOException {
        source.expectInt(6); // version
        source.skip(4); // indexSize
        var headers = source.readBytes(source.readInt() - 4);
        var entries = source.readObjects(source.readInt(), SoundEntry::read);

        return new SoundContainer(
            headers,
            entries
        );
    }
}
