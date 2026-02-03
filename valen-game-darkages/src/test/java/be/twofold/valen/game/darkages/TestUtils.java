package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(Function<LoadingContext, AssetReader<?, DarkAgesAsset>> readerFunction) throws IOException {
        DarkAgesGame game = new DarkAgesGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var loader = game.open(archiveName);
            var reader = readerFunction.apply(loader);
            readAllInMap(loader, reader);
        }
    }

    private static void readAllInMap(AssetLoader loader, AssetReader<?, DarkAgesAsset> reader) {
        var entries = loader.archive().all()
                .filter(asset -> asset.size() != 0 && reader.canRead((DarkAgesAsset) asset))
            .sorted(Comparator.naturalOrder())
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (Asset asset : entries) {
            try {
                var fromBytes = loader.load(asset.id(), Bytes.class);
                reader.read(BinarySource.wrap(fromBytes), (DarkAgesAsset) asset, loader);
            } catch (FileNotFoundException e) {
                System.err.println("File not found" + asset.id().fullName());
            } catch (Exception e) {
                fail(asset.id().fullName(), e);
            }
        }
    }
}
