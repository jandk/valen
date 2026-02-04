package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class EternalStorageManager extends StorageManager {
    private final Map<Long, StorageLocation.FileSlice> streamIndex;

    public EternalStorageManager(
        Map<FileId, BinarySource> sources,
        Set<FileId> sharedIds,
        Decompressor oodle,
        Map<Long, StorageLocation.FileSlice> streamIndex
    ) {
        super(sources, sharedIds, oodle); // TODO: Fix decompressors
        this.streamIndex = Map.copyOf(streamIndex);
    }

    @Override
    protected Bytes openCustom(StorageLocation location) throws IOException {
        if (!(location instanceof EternalStreamLocation streamLocation)) {
            return super.openCustom(location);
        }

        var slice = streamIndex.get(streamLocation.streamId());
        if (slice == null) {
            throw new FileNotFoundException("Stream not found: " + streamLocation.streamId());
        }

        if (streamLocation.size() > 0) {
            return open(new StorageLocation.Compressed(slice, "oodle", streamLocation.size()));
        } else {
            return open(slice);
        }
    }
}
