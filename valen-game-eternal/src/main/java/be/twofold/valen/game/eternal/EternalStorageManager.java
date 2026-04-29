package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EternalStorageManager extends StorageManager {
    private final Map<Long, Location.FileSlice> streamIndex;

    public EternalStorageManager(
        Map<Path, BinarySource> sources,
        Set<Path> shared,
        Map<Long, Location.FileSlice> streamIndex
    ) {
        super(sources, shared); // TODO: Fix decompressors
        this.streamIndex = Map.copyOf(streamIndex);
    }

    @Override
    protected Bytes openCustom(Location.Custom custom) throws IOException {
        if (!(custom instanceof EternalStreamLocation streamLocation)) {
            return super.openCustom(custom);
        }

        var slice = streamIndex.get(streamLocation.streamId());
        if (slice == null) {
            return Bytes.empty();
        }

        if (streamLocation.size() > 0 && streamLocation.size() != slice.size()) {
            return open(new Location.Compressed(slice, CompressionType.OODLE, streamLocation.size()));
        } else {
            return open(slice);
        }
    }
}
