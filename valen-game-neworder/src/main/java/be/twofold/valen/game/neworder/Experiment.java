package be.twofold.valen.game.neworder;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.neworder.index.*;
import be.twofold.valen.game.neworder.master.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public final class Experiment {
    public static void main(String[] args) throws IOException {
        var base = Path.of("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Wolfenstein.The.New.Order\\base");

        var containers = Master.read(base.resolve("master.index")).containers();
        containers.forEach(System.out::println);

        var typeNames = new TreeMap<String, Integer>();
        for (MasterContainer container : containers) {
            Index index = Index.read(base.resolve(container.indexName()));

            try (var resourceSource = DataSource.fromPath(base.resolve(container.resourceName()))) {
                for (IndexEntry entry : index.entries()) {
                    typeNames.merge(entry.typeName(), 1, Integer::sum);
                    if (!entry.platformStreamData().isEmpty()) {
                        System.out.println("Platform stream data: " + entry.platformStreamData());
                    }

                    if (entry.typeName().equals("skeleton")) {
                        System.out.print(entry.resourceName());

                        resourceSource.seek(entry.offset());
                        var data = resourceSource.readBytes(entry.compressedLength());
                        var inflated = Compression.InflateRaw.decompress(ByteBuffer.wrap(data), entry.uncompressedLength());
                        System.out.println(" ");
                    }
                }
            }
        }

        typeNames.forEach((k, v) -> System.out.println(k + ": " + v));
    }

}
