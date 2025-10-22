package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(Function<DyingLightArchive, AssetReader<?, DyingLightAsset>> readerFunction) throws IOException {
        DyingLightGame game = new DyingLightGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var archive = game.loadArchive(archiveName);
            var reader = readerFunction.apply(archive);
            readAllInMap(archive, reader);
        }
    }

    private static void readAllInMap(DyingLightArchive archive, AssetReader<?, DyingLightAsset> reader) {
        var entries = archive.getAll()
            .filter(asset -> asset.size() != 0 && reader.canRead(asset))
            .sorted(Comparator.naturalOrder())
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (DyingLightAsset asset : entries) {
            try {
                var bytes = archive.loadAsset(asset.id(), Bytes.class);
                reader.read(BinaryReader.fromBytes(bytes), asset);
            } catch (FileNotFoundException e) {
                System.err.println("File not found" + asset.id().fullName());
            } catch (Exception e) {
                fail(asset.id().fullName(), e);
            }
        }
    }
}
