package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GreatCircleStorageManager extends StorageManager {
    private final Map<Long, Location> streamIndex;

    public GreatCircleStorageManager(
        Map<Path, BinarySource> sources,
        Set<Path> shared,
        Map<Long, Location> streamIndex,
        Decompressors decompressors
    ) {
        super(sources, shared, decompressors);
        this.streamIndex = Map.copyOf(streamIndex);
    }

    @Override
    protected Bytes openCustom(Location.Custom custom) throws IOException {
        if (!(custom instanceof GreatCircleStreamLocation(long streamId, int size))) {
            return super.openCustom(custom);
        }

        var location = streamIndex.get(streamId);
        if (location == null) {
            return Bytes.empty();
        }

        if (location instanceof Location.Compressed compressed) {
            return size > 0
                ? open(new Location.Compressed(compressed.base(), compressed.type(), size))
                : open(compressed.base());
        }
        return open(location);
    }
}
