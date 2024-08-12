package be.twofold.valen;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.*;
import be.twofold.valen.reader.*;

import java.io.*;
import java.nio.file.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(Function<Archive<?>, ResourceReader<?>> readerFunction) throws IOException {
        EternalGame game = new EternalGameFactory().load(Path.of(Constants.ExecutablePath));

        for (String archiveName : game.archiveNames()) {
            var archive = game.loadArchive(archiveName);
            var reader = readerFunction.apply(archive);
            readAllInMap(archive, reader);
        }

        var reader = readerFunction.apply(manager);

        for (var map : manager.getSpec().maps()) {
            manager.select(map);
            readAllInMap(manager, reader);
        }
    }

    private static void readAllInMap(EternalArchive archive, ResourceReader<?> reader) {
        var entries = archive.assets().stream()
            .filter(asset -> asset.size() != 0 && reader.canRead(asset))
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        entries.forEach(resource -> assertThatNoException()
            .isThrownBy(() -> {
                var buffer = new ByteArrayDataSource(archive.readRawResource(resource));
                reader.read(buffer, resource);
            }));
    }

}
