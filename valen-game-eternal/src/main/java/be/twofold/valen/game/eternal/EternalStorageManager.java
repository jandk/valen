package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.io.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class EternalStorageManager extends StorageManager {
    private final Map<Long, Location.FileSlice> streamIndex;

    public EternalStorageManager(
        Map<FileId, BinarySource> sources,
        Set<FileId> sharedIds,
        Map<Long, Location.FileSlice> streamIndex
    ) {
        super(sources, sharedIds); // TODO: Fix decompressors
        this.streamIndex = Map.copyOf(streamIndex);
    }

    @Override
    protected Bytes openCustom(Location location) throws IOException {
        if (!(location instanceof EternalStreamLocation streamLocation)) {
            return super.openCustom(location);
        }

        var slice = streamIndex.get(streamLocation.streamId());
        if (slice == null) {
            throw new FileNotFoundException("Stream not found: " + streamLocation.streamId());
        }

        if (streamLocation.size() > 0) {
            return open(new Location.Compressed(slice, CompressionType.OODLE, streamLocation.size()));
        } else {
            return open(slice);
        }
    }
}
