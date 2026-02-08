package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(AssetReader<?, GreatCircleAsset> reader) throws IOException {
        GreatCircleGame game = new GreatCircleGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var loader = game.open(archiveName);
            readAllInMap(loader, reader);
        }
    }

    private static void readAllInMap(AssetLoader loader, AssetReader<?, GreatCircleAsset> reader) {
        var entries = loader.archive().all()
                .filter(asset -> asset.location().size() != 0 && reader.canRead((GreatCircleAsset) asset))
            .sorted()
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (Asset asset : entries) {
            try {
                var bytes = loader.load(asset.id(), Bytes.class);
                reader.read(BinarySource.wrap(bytes), (GreatCircleAsset) asset, loader);
            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                if (!e.getMessage().startsWith("Decompression failed")) {
                    fail("Failure when reading " + asset.id().fullName(), e);
                }
            } catch (Exception e) {
                fail("Failure when reading " + asset.id().fullName(), e);
            }
        }
    }

}
