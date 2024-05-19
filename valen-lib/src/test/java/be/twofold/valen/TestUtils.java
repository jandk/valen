package be.twofold.valen;

import be.twofold.valen.core.io.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;

import java.io.*;
import java.nio.file.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public abstract class TestUtils {

    public static void testReader(Function<FileManager, ResourceReader<?>> readerFunction) throws IOException {
        var manager = DaggerManagerFactory.create()
            .fileManager()
            .load(Path.of(Constants.BasePath));

        var reader = readerFunction.apply(manager);

        for (var map : manager.getSpec().maps()) {
            manager.select(map);
            readAllInMap(manager, reader);
        }
    }

    private static void readAllInMap(FileManager manager, ResourceReader<?> reader) {
        var entries = manager.getEntries().stream()
            .filter(entry -> entry.uncompressedSize() != 0 && reader.canRead(entry))
            .toList();

        System.out.println("Trying to read " + entries.size() + " entries");

        entries.forEach(resource -> assertThatNoException()
            .isThrownBy(() -> {
                var buffer = new ByteArrayDataSource(manager.readRawResource(resource));
                reader.read(buffer, resource);
            }));
    }

}
