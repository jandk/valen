package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.reader.streamdb.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public final class DarkAgesStorageManager extends StorageManager {
    private final List<StreamDbFile> streamDbs;

    public DarkAgesStorageManager(
        Map<FileId, BinarySource> sources,
        List<StreamDbFile> streamDbs,
        Decompressor oodle
    ) {
        super(sources, oodle); // TODO: Fix decompressors
        this.streamDbs = Check.nonNull(streamDbs, "streamDbs");
    }

    @Override
    protected Bytes openCustom(StorageLocation location) throws IOException {
        return switch (location) {
            case DarkAgesStreamLocation streamLocation -> openStream(streamLocation);
            default -> throw new IllegalStateException("Unexpected value: " + location);
        };
    }

    private Bytes openStream(DarkAgesStreamLocation location) throws IOException {
        for (StreamDbFile streamDb : streamDbs) {
            if (streamDb.exists(location.streamId())) {
                return streamDb.read(location.streamId(), location.size());
            }
        }
        throw new FileNotFoundException("Stream not found: " + location.streamId());
    }

    @Override
    protected void onClose() throws IOException {
        for (StreamDbFile streamDb : streamDbs) {
            streamDb.close();
        }
    }
}
