package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(AssetReader<?, DoomAsset> reader) throws IOException {
        try (var game = new DoomGameFactory().load(Path.of(Constants.ExecutablePath))) {
            // Assets can show up in more than one archive, only read them once
            var seen = new HashSet<AssetID>();
            for (var archiveName : game.archiveNames()) {
                try (var loader = game.open(archiveName)) {
                    readAll(loader, reader, seen);
                }
            }
        }
    }

    private static void readAll(AssetLoader loader, AssetReader<?, DoomAsset> reader, Set<AssetID> seen) {
        var entries = loader.all()
            .filter(asset -> asset instanceof DoomAsset doomAsset
                && reader.canRead(doomAsset)
                && seen.add(asset.id()))
            .map(DoomAsset.class::cast)
            .sorted(Comparator.naturalOrder())
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (var asset : entries) {
            try {
                reader.read(asset, loader);
            } catch (Exception e) {
                fail(asset.id().fullName(), e);
            }
        }
    }
}
