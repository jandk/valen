package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.deathloop.index.*;
import be.twofold.valen.game.deathloop.master.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Experiment {
    public static void main(String[] args) throws IOException {
        var root = Path.of("D:\\Games\\Steam\\steamapps\\common\\DEATHLOOP");
        var base = root.resolve("base");

        var masterIndex = MasterIndex.read(base.resolve("master.index"));
        System.out.println(masterIndex);

        var index = Index.read(base.resolve(masterIndex.indexFile()));
        System.out.println(index);

        var sources = new ArrayList<DataSource>();
        for (var dataFile : masterIndex.dataFiles()) {
            sources.add(DataSource.fromPath(base.resolve(dataFile)));
        }

        Map<String, List<IndexEntry>> collect = index.entries().stream()
            .collect(Collectors.groupingBy(IndexEntry::typeName));

        System.out.println(collect.size());

        var image = index.entries().stream()
            .filter(e -> e.resourceName().equals("models/environment/texture/tile/fabric/carpet/carpet_soft_white_01_d.png"))
            .findFirst().orElseThrow();

        DataSource source = sources.get(image.fileId());
        source.seek(image.offset());

//        var data = source.readBytes(image.compressedLength());
//        ByteBuffer buffer = Decompressor.forType(CompressionType.KrakenChunked).decompress(ByteBuffer.wrap(data), image.uncompressedLength());
//        Files.write(root.resolve("carpet_soft_white_01_d.bimage"), buffer.array());
//        System.out.println(data.length);
    }

}
