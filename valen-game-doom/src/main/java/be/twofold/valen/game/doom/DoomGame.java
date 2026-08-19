package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.megatexture.*;
import be.twofold.valen.game.doom.megatexture.vmtr.*;
import be.twofold.valen.game.doom.readers.image.*;
import be.twofold.valen.game.doom.readers.model.*;
import be.twofold.valen.game.doom.resources.*;
import be.twofold.valen.game.doom.vmtr.*;
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
    private final List<VmtrEntry> vmtrEntries;
    private final Mega2Grid pages;
    private final PageStitcher stitcher;

    DoomGame(Path path) throws IOException {
        this.base = path.resolve("base");

        // The grid goes last, so nothing that can fail comes after the one thing that must be closed.
        var virtualTextures = path.resolve("virtualtextures");
        this.vmtrEntries = Vmtr.readAll(virtualTextures);
        this.pages = Mega2Grid.open(virtualTextures);
        this.stitcher = new PageStitcher(pages);
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

        var vmtrAssets = vmtrEntries.stream()
            .flatMap(entry -> Arrays.stream(VmtrLayer.values())
                .map(layer -> new DoomAsset.Vmtr(entry, layer)))
            .toList();

        var archive = Archive.combine(List.of(
            Archive.of(assets),
            Archive.of(vmtrAssets)));

        var storageManager = new StorageManager(
            Map.of(resourcesPath, resources),
            Set.of(),
            new Decompressors(null)
        );

        var readers = new ArrayList<>(READERS);
        readers.addFirst(new VmtrReader(stitcher));

        return new AssetLoader(
            archive,
            storageManager,
            List.copyOf(readers)
        );
    }

    private DoomAsset.Resource mapResourceEntry(ResourcesIndexEntry entry, String fileName, Path path) {
        var id = new DoomAssetID(fileName);

        Location location = new Location.FileSlice(path, entry.offset(), entry.sizeCompressed());
        if (entry.size() != entry.sizeCompressed()) {
            location = new Location.Compressed(location, CompressionType.DEFLATE_RAW, entry.size());
        }

        return new DoomAsset.Resource(id, entry.typeName(), location);
    }

    @Override
    public void close() throws IOException {
        pages.close();
    }
}
