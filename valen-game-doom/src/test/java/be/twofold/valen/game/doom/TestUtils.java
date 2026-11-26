package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {
    public static void testReader(AssetReader<?, DoomAsset> reader) throws IOException {
        testReader(reader, Set.of());
    }

    public static void testReader(AssetReader<?, DoomAsset> reader, Set<String> skip) throws IOException {
        testAssets(reader::canRead, skip, reader::read);
    }

    public static void testAssets(Predicate<DoomAsset> filter, Set<String> skip, AssetTest test) throws IOException {
        try (var game = new DoomGameFactory().load(Path.of(Constants.ExecutablePath))) {
            // Assets can show up in more than one archive, only read them once
            var seen = new HashSet<AssetID>();
            for (var archiveName : game.archiveNames()) {
                try (var loader = game.open(archiveName)) {
                    testAll(loader, filter, skip, seen, test);
                }
            }
        }
    }

    private static void testAll(AssetLoader loader, Predicate<DoomAsset> filter, Set<String> skip, Set<AssetID> seen, AssetTest test) {
        var entries = loader.all()
            .filter(asset -> asset instanceof DoomAsset doomAsset
                && filter.test(doomAsset)
                && !skip.contains(asset.id().fullName())
                && seen.add(asset.id()))
            .map(DoomAsset.class::cast)
            .sorted(Comparator.naturalOrder())
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (var asset : entries) {
            try {
                test.test(asset, loader);
            } catch (Exception e) {
                fail(asset.id().fullName(), e);
            }
        }
    }

    @FunctionalInterface
    public interface AssetTest {
        void test(DoomAsset asset, AssetLoader loader) throws Exception;
    }
}
