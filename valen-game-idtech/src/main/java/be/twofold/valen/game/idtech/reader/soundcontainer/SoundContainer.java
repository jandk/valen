package be.twofold.valen.game.idtech.reader.soundcontainer;

import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record SoundContainer(
    Bytes headers,
    List<SoundEntry> entries
) {
    public static SoundContainer read(BinaryReader reader, IdTechGame game) throws IOException {
        reader.expectInt(6); // version
        reader.skip(4); // indexSize
        var headers = reader.readBytes(reader.readInt() - 4);
        var entries = reader.readObjects(reader.readInt(), s -> SoundEntry.read(s, game));

        return new SoundContainer(
            headers,
            entries
        );
    }
}
