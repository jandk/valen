package be.twofold.valen.game.doom.resources;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;

import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        var indexPath = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOM\\base\\gameresources.index");
        var index = ResourcesIndex.read(indexPath);
        System.out.println("Header: " + index.header());
        System.out.println("Entries: " + index.entries().size());

        try (var source = DataSource.fromPath(Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOM\\base\\gameresources.resources"))) {
            for (var entry : index.entries()) {
                if (entry.fileName().isEmpty() || entry.fileId() != 0) {
                    continue;
                }

                source.seek(entry.offset());
                byte[] compressed = source.readBytes(entry.sizeCompressed());
                byte[] decompressed = compressed;

                if (entry.size() != entry.sizeCompressed()) {
                    decompressed = Compression.InflateRaw.decompress(compressed, entry.size());
                }

                var dest = Path.of("D:\\Projects\\2016\\Extracted").resolve(entry.fileName());
                System.out.println(dest);

                Files.createDirectories(dest.getParent());
                Files.write(dest, decompressed);
            }
        }
    }
}
