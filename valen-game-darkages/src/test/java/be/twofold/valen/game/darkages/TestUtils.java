package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(AssetReader.Binary<?, DarkAgesAsset> reader) throws IOException {
        DarkAgesGame game = new DarkAgesGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var loader = game.open(archiveName);
            readAllInMap(loader, reader);
        }
    }

    private static void readAllInMap(AssetLoader loader, AssetReader.Binary<?, DarkAgesAsset> reader) {
        var entries = loader.archive().all()
            .filter(asset -> asset.location().size() != 0 && reader.canRead((DarkAgesAsset) asset))
            .sorted(Comparator.naturalOrder())
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (Asset asset : entries) {
            try {
                reader.read((DarkAgesAsset) asset, loader);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + asset.id().fullName());
            } catch (Exception e) {
                fail(asset.id().fullName(), e);
            }
        }
    }
}
