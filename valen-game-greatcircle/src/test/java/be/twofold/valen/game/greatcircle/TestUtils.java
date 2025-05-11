package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(Function<GreatCircleArchive, AssetReader<?, GreatCircleAsset>> readerFunction) throws IOException {
        GreatCircleGame game = new GreatCircleGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var archive = game.loadArchive(archiveName);
            var reader = readerFunction.apply(archive);
            readAllInMap(archive, reader);
        }
    }

    private static void readAllInMap(GreatCircleArchive archive, AssetReader<?, GreatCircleAsset> reader) {
        var entries = archive.getAll()
            .filter(asset -> asset.size() != 0 && reader.canRead(asset))
            .sorted()
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (GreatCircleAsset asset : entries) {
            try {
                var buffer = archive.loadAsset(asset.id(), ByteBuffer.class);
                reader.read(DataSource.fromBuffer(buffer), asset);
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
