package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(Function<DarkAgesArchive, AssetReader<?, DarkAgesAsset>> readerFunction) throws IOException {
        DarkAgesGame game = new DarkAgesGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var archive = game.loadArchive(archiveName);
            var reader = readerFunction.apply(archive);
            readAllInMap(archive, reader);
        }
    }

    private static void readAllInMap(DarkAgesArchive archive, AssetReader<?, DarkAgesAsset> reader) {
        var entries = archive.getAll()
            .filter(asset -> asset.size() != 0 && reader.canRead(asset))
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        for (DarkAgesAsset asset : entries) {
            try {
                var buffer = archive.loadAsset(asset.id(), ByteBuffer.class);
                reader.read(DataSource.fromBuffer(buffer), asset);
            } catch (FileNotFoundException e) {
                System.err.println("File not found");
            } catch (Exception e) {
                fail(e);
            }
        }
    }

}
