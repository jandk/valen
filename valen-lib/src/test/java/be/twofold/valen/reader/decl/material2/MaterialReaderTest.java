package be.twofold.valen.reader.decl.material2;

import be.twofold.valen.*;
import be.twofold.valen.reader.decl.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

class MaterialReaderTest {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> {
            var declReader = new DeclReader(manager.resourceManager);
            return new MaterialReader(() -> manager, manager.resourceManager, declReader);
        });

        try (var writer = Files.newBufferedWriter(Path.of("D:\\missing.txt"))) {
            writer.write("Missing images:");
            writer.newLine();
            for (var entry : MaterialReader.MissingImages.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }

            writer.newLine();
            writer.write("Material kind counts:");
            writer.newLine();

            for (var entry : MaterialReader.MaterialKindCounts.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        }
    }
}
