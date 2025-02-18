package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(Function<EternalArchive, AssetReader<?, EternalAsset>> readerFunction) throws IOException {
        EternalGame game = new EternalGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var archive = game.loadArchive(archiveName);
            var reader = readerFunction.apply(archive);
            readAllInMap(archive, reader);
        }
    }

    private static void readAllInMap(EternalArchive archive, AssetReader<?, EternalAsset> reader) {
        var entries = archive.assets()
            .filter(asset -> asset.size() != 0 && reader.canRead((EternalAsset) asset))
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        entries.forEach(asset -> assertThatNoException()
            .isThrownBy(() -> {
                var bytes = archive.loadAsset(asset.id(), byte[].class);
                reader.read(DataSource.fromArray(bytes), (EternalAsset) asset);
            }));
    }

}
