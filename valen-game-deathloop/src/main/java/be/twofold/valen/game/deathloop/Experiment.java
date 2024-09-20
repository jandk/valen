package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.deathloop.image.*;
import be.twofold.valen.game.deathloop.index.*;
import be.twofold.valen.game.deathloop.master.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Experiment {
    public static void main(String[] args) throws IOException {
        var root = Path.of("D:\\Projects\\Deathloop\\DEATHLOOP");
        var base = root.resolve("base");

        var masterIndex = MasterIndex.read(base.resolve("master.index"));
        System.out.println(masterIndex);

        var index = Index.read(base.resolve(masterIndex.indexFile()));
        System.out.println(index);

        var sources = new ArrayList<DataSource>();
        for (var dataFile : masterIndex.dataFiles()) {
            sources.add(DataSource.fromPath(base.resolve(dataFile)));
        }

        var collect = index.entries().stream()
            .collect(Collectors.groupingBy(IndexEntry::typeName));

        var outBase = Path.of("D:\\Projects\\Deathloop\\Extracted");
        for (var entry : collect.get("image")) {
//            var resolved = outBase
//                .resolve(entry.typeName())
//                .resolve(entry.fileName());

//            if (!Files.exists(resolved)) {
//                Files.createDirectories(resolved.getParent());
//
//
//                Files.write(resolved, uncompressed);
//            }

            if (entry.useBits() != 0) {
                continue;
            }

            var source = sources.get(entry.fileId());
            source.seek(entry.offset());
            var compressed = source.readBytes(entry.compressedLength());
            var uncompressed = entry.compressedLength() != entry.uncompressedLength()
                ? Compression.OodleChunked.decompress(ByteBuffer.wrap(compressed), entry.uncompressedLength()).array()
                : compressed;

            Texture texture = new ImageReader().read(DataSource.fromArray(uncompressed));
            System.out.println(texture);
        }
    }

}
