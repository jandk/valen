package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ColossusStorageManager extends StorageManager {
    private final Map<Long, List<Location.FileSlice>> streamIndex;

    public ColossusStorageManager(
        Map<Path, BinarySource> sources,
        Set<Path> shared,
        Map<Long, List<Location.FileSlice>> streamIndex
    ) {
        super(sources, shared);
        this.streamIndex = Map.copyOf(streamIndex);
    }

    @Override
    protected Bytes openCustom(Location.Custom custom) throws IOException {
        if (!(custom instanceof ColossusStreamLocation streamLocation)) {
            return super.openCustom(custom);
        }

        var slices = streamIndex.get(streamLocation.streamId());
        if (slices == null || streamLocation.tier() >= slices.size()) {
            return Bytes.empty();
        }

        var slice = slices.get(streamLocation.tier());
        var sized = new Location.FileSlice(slice.path(), slice.offset(), streamLocation.compressedSize());
        if (streamLocation.compressedSize() != streamLocation.uncompressedSize()) {
            return open(new Location.Compressed(sized, CompressionType.OODLE, streamLocation.uncompressedSize()));
        }
        return open(sized);
    }
}
