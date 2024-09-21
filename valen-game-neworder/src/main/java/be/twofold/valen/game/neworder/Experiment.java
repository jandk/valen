package be.twofold.valen.game.neworder;

import be.twofold.valen.core.util.*;
import be.twofold.valen.game.neworder.index.*;
import be.twofold.valen.game.neworder.master.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class Experiment {
    public static void main(String[] args) throws IOException {
        var path = Path.of("D:\\Games\\Steam\\steamapps\\common\\Wolfenstein.The.New.Order\\WolfNewOrder_x64.exe");
        var base = path.getParent().resolve("base");

        var containers = Master.read(base.resolve("master.index")).containers();
        containers.forEach(System.out::println);

        var entries = new ArrayList<IndexEntry>();
        var typeNames = new TreeMap<String, Integer>();
        for (var container : containers) {
            var index = Index.read(base.resolve(container.indexName()));
            entries.addAll(index.entries());

//            var outBasePath = Path.of("D:\\Projects\\Wolfenstein New Order\\Extracted");
//            try (var source = DataSource.fromPath(base.resolve(container.resourceName()))) {
//                for (var entry : index.entries()) {
//                    typeNames.merge(entry.typeName(), 1, Integer::sum);
//                    if (entry.typeName().equals("image")) {
//                        var destPath = outBasePath
//                            .resolve(entry.typeName())
//                            .resolve(entry.fileName());
//
//                        if (!Files.exists(destPath)) {
//                            Files.createDirectories(destPath.getParent());
//
//                            source.seek(entry.offset());
//                            byte[] compressed = source.readBytes(entry.compressedLength());
//                            var uncompressed = entry.uncompressedLength() != entry.compressedLength()
//                                ? Compression.InflateRaw.decompress(ByteBuffer.wrap(compressed), entry.uncompressedLength()).array()
//                                : compressed;
//
//                            Files.write(destPath, uncompressed);
//                        }
//                    }
////                    if (!entry.platformStreamData().isEmpty()) {
////                        System.out.println("Platform stream data: " + entry.platformStreamData());
////                    }
//                }
//            }
        }

        typeNames.forEach((k, v) -> System.out.println(k + ": " + v));

        var csv = CsvUtils.toCsv(List.of(), entries, IndexEntry.class);
        Files.writeString(Path.of("D:\\Projects\\Wolfenstein New Order\\index.csv"), csv);
    }

}
