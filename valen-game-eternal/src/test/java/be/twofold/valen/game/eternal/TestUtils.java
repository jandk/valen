package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(AssetReader<?, EternalAsset> reader) throws IOException {
        EternalGame game = new EternalGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var loader = game.open(archiveName);
            readAllInMap(loader, reader);
        }
    }

    private static void readAllInMap(AssetLoader loader, AssetReader<?, EternalAsset> reader) {
        var entries = loader.archive().all()
            .filter(asset -> asset.size() != 0 && reader.canRead((EternalAsset) asset))
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (Asset asset : entries) {
            try {
                var bytes = loader.load(asset.id(), Bytes.class);
                reader.read(BinarySource.wrap(bytes), (EternalAsset) asset, loader);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + asset.id().fullName());
            } catch (Exception e) {
                fail(asset.id().fullName(), e);
            }
        }
    }
}
