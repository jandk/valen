package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

public class DLBoneMap {
    public static Shorts read(BinaryReader reader) throws IOException {
        var offset = FlaggedOffset.read(reader);
        var count = reader.readLong();
        if (!offset.isValid()) {
            Check.state(count == 0, "Expected zero count for invalid bone map offset");
            return Shorts.wrap(new short[0]);
        }
        Check.state(count > 0, "Expected positive count for valid bone map offset");
        var currentPos = reader.position();
        reader.position(offset.offset());
        var indices = reader.readShorts((int) count);
        reader.position(currentPos);
        return Shorts.wrap(indices);
    }
}
