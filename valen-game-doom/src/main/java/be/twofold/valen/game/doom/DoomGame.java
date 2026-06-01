package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.doom.readers.image.*;
import be.twofold.valen.game.doom.readers.model.*;
import be.twofold.valen.game.doom.resources.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DoomGame implements Game {
    private static final Set<String> KNOWN_TYPES = Set.of(
        "image",
        "model", "baseModel",
        "skeleton",
        "material", "decalatlas", "transsortatlas",
        "anim"
    );

    private static final List<AssetReader<?, DoomAsset>> READERS = List.of(
        new ImageReader(),
        new ModelReader()
    );

    private final Path base;

    DoomGame(Path path) {
        this.base = path.resolve("base");
    }

    @Override
    public List<String> archiveNames() {
        return List.of(
            "gameresources",
            "snap_gameresources"
        );
    }

    @Override
    public AssetLoader open(String name) throws IOException {
        var index = ResourcesIndex.read(base.resolve(name + ".index"));
        var resourcesPath = base.resolve(name + ".resources");
        var resources = BinarySource.open(resourcesPath);

        var uniqueNames = new HashSet<String>(index.entries().size());
        var assets = new ArrayList<DoomAsset>();
        for (var entry : index.entries()) {
            if (entry.size() == 0) {
                continue;
            }

            var fileName = KNOWN_TYPES.contains(entry.typeName())
                ? entry.resourceName() : entry.fileName();
            if (!uniqueNames.add(fileName)) {
                continue;
            }

            assets.add(mapResourceEntry(entry, fileName, resourcesPath));
        }

        return new AssetLoader(
            Archive.of(assets),
            new StorageManager(Map.of(
                resourcesPath, resources
            ), Set.of()),
            List.copyOf(READERS)
        );
    }

    private DoomAsset mapResourceEntry(ResourcesIndexEntry entry, String fileName, Path path) {
        var id = new DoomAssetID(fileName);

        Location location = new Location.FileSlice(path, entry.offset(), entry.sizeCompressed());
        if (entry.size() != entry.sizeCompressed()) {
            location = new Location.Compressed(location, CompressionType.DEFLATE_RAW, entry.size());
        }

        return new DoomAsset(id, entry.typeName(), location);
    }
}
