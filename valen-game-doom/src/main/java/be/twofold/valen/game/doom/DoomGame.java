package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.megatexture.*;
import be.twofold.valen.game.doom.megatexture.vmtr.*;
import be.twofold.valen.game.doom.readers.image.*;
import be.twofold.valen.game.doom.readers.md6skel.*;
import be.twofold.valen.game.doom.readers.staticmodel.*;
import be.twofold.valen.game.doom.resources.*;
import be.twofold.valen.game.doom.vmtr.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DoomGame implements Game {
    private static final Set<ResourceType> KNOWN_TYPES = Set.of(
        ResourceType.of("image"),
        ResourceType.of("model"), ResourceType.of("baseModel"),
        ResourceType.of("skeleton"),
        ResourceType.of("material"), ResourceType.of("decalatlas"), ResourceType.of("transsortatlas"),
        ResourceType.of("anim")
    );

    private static final List<AssetReader<?, DoomAsset>> READERS = List.of(
        new ImageReader(),
        new Md6SkelReader(),
        new StaticModelReader()
    );
    public static final String SOUND_SOUNDBANKS_PC = "sound/soundbanks/pc";

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
            "snap_gameresources",
            SOUND_SOUNDBANKS_PC
        );
    }

    @Override
    public AssetLoader open(String name) throws IOException {
        if (name.equals(SOUND_SOUNDBANKS_PC)) {
            return WwiseLoader.load(base.resolve(SOUND_SOUNDBANKS_PC));
        }

        var pindexPath = base.resolve(name + ".pindex");
        var index = Files.exists(pindexPath)
            ? ResourcesIndex.read(pindexPath)
            : ResourcesIndex.read(base.resolve(name + ".index"));
        var dataPaths = List.of(
            base.resolve(name + ".resources"),
            base.resolve(name + ".patch")
        );

        var sources = new HashMap<Path, BinaryReader>();
        for (var dataPath : dataPaths) {
            if (Files.exists(dataPath)) {
                sources.put(dataPath, BinaryReader.open(dataPath));
            }
        }

        var uniqueIds = new HashSet<DoomAssetID>(index.entries().size());
        var assets = new ArrayList<DoomAsset>();
        for (var entry : index.entries()) {
            if (entry.size() == 0) {
                continue;
            }

            var type = ResourceType.of(entry.typeName());
            var assetName = KNOWN_TYPES.contains(type)
                ? entry.resourceName() : entry.fileName();
            var id = new DoomAssetID(assetName, type);
            if (!uniqueIds.add(id)) {
                continue;
            }

            assets.add(mapResourceEntry(entry, id, dataPaths.get(entry.fileId())));
        }

        var vmtrAssets = vmtrEntries.stream()
            .flatMap(entry -> Arrays.stream(VmtrLayer.values())
                .map(layer -> new DoomAsset.Vmtr(entry, layer)))
            .toList();

        var archive = Archive.combine(List.of(
            Archive.of(assets),
            Archive.of(vmtrAssets)));

        var storageManager = new StorageManager(
            sources,
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

    private DoomAsset.Resource mapResourceEntry(ResourcesIndexEntry entry, DoomAssetID id, Path path) {
        Location location = new Location.FileSlice(path, entry.offset(), entry.sizeCompressed());
        if (entry.size() != entry.sizeCompressed()) {
            location = new Location.Compressed(location, CompressionType.DEFLATE_RAW, entry.size());
        }

        return new DoomAsset.Resource(id, location);
    }

    @Override
    public void close() throws IOException {
        pages.close();
    }
}
