package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.dds.*;
import be.twofold.valen.format.granite.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

public final class GustavGame implements Game {
    private static final Logger log = LoggerFactory.getLogger(GustavGame.class);
    private static final List<AssetReader<?, ?>> ASSET_READERS = List.of(
        DdsImporter.create()
    );

    private final Path dataPath;

    public GustavGame(Path path) {
        this.dataPath = path.resolve("Data");
    }

    @Override
    public List<String> archiveNames() {
        try (var stream = Files.find(dataPath, 2, this::matchPath)) {
            return stream
                .map(p -> Filenames.removeExtension(dataPath.relativize(p).toString()))
                .toList();
        } catch (IOException e) {
            log.error("Failed to get archive names", e);
            return List.of();
        }
    }

    @Override
    public AssetLoader open(String name) throws IOException {
        var resolved = dataPath.resolve(name + ".pak");
        var pakFileIndex = PakFileIndex.build(resolved);

        var mainArchive = Archive.simple(pakFileIndex.index());
        var gtsAssets = mainArchive.all()
            .filter(asset -> asset.id().fullName().endsWith(".gts"))
            .toList();
        var storageManager = new StorageManager(
            pakFileIndex.sources(),
            Set.of()
        );
        GraniteArchive graniteArchive = new GraniteArchive(gtsAssets, storageManager, gtpName -> {
            var assetId = new GustavAssetID(gtpName);
            var gtpAsset = mainArchive.get(assetId).orElseThrow();
            return BinarySource.wrap(storageManager.open(gtpAsset.location()));
        });

        var archive = Archive.combine(List.of(mainArchive, graniteArchive));

        var readers = new ArrayList<>(ASSET_READERS);
        readers.addFirst(new GraniteReader(graniteArchive.getContainers()));

        return new AssetLoader(
            archive,
            storageManager,
            List.copyOf(readers)
        );
    }

    private boolean matchPath(Path path, BasicFileAttributes attrs) {
        return Files.isRegularFile(path)
            && path.getFileName().toString().endsWith(".pak")
            && !path.getFileName().toString().matches("\\w+_\\d+\\.pak");
    }
}
