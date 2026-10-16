package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.soundcontainer.*;
import be.twofold.valen.game.eternal.reader.soundmetadata.*;
import be.twofold.valen.middleware.wwise.shared.*;
import be.twofold.valen.middleware.wwise.wem.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record SoundbankIndex(
) {
    private static final List<AssetReader<?, ?>> READERS = List.of(
        new SoundReader(),
        new WemReader()
    );

    public static AssetLoader build(Path path, Decompressors decompressors) throws IOException {
        var metadata = SoundMetadata.load(path.resolve("soundmetadata.bin"));

        Map<Path, BinarySource> sources = new HashMap<>();
        List<Asset> assets = new ArrayList<>();
        for (var container : metadata.containers()) {
            var name = container.name();
            var dot = name.lastIndexOf('.');
            for (var i = 0; i < container.files().size(); i++) {
                var file = container.files().get(i);
                var filename = i == 0
                    ? name
                    : name.substring(0, dot) + "_patch_" + i + name.substring(dot);

                var resolved = path.resolve(filename);
                var source = BinarySource.open(resolved);
                sources.put(resolved, source);

                var snd = SoundContainer.read(source);
                for (var j = 0; j < snd.entries().size(); j++) {
                    var entry = snd.entries().get(j);
                    var live = ((file.mask().get(j / 32) >>> (j % 32)) & 1) != 0;
                    if (!live || entry.sizeOnDisk() == 0) {
                        continue;
                    }


                    var assetId = new WwiseAssetId(entry.mediaId(), entry.mediaId() + ".wem");
                    var location = new Location.FileSlice(resolved, entry.offset(), entry.sizeOnDisk());

                    Asset asset = switch (entry.type()) {
                        case WEM -> new WwiseAsset(assetId, location);
                        case OPUS -> {
                            var header = source
                                .position(entry.headerOffset() + 12)
                                .readBytes(entry.headerSize());

                            yield new SoundAsset(assetId, location, header);
                        }
                    };

                    assets.add(asset);
                }
            }
        }

        var archive = Archive.of(assets);
        var storageManager = new StorageManager(sources, Set.of(), decompressors);
        return new AssetLoader(archive, storageManager, READERS);
    }
}
