package be.twofold.valen.game.darkages;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.reader.image.*;
import be.twofold.valen.game.darkages.reader.packagemapspec.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.darkages.reader.streamdb.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Experiment {
    private static final Decompressor DECOMPRESSOR = Decompressor.oodle(Path.of("oo2core_9_win64.dll"));

    public static void main(String[] args) throws Exception {
        var path = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOMTheDarkAges\\DOOMTheDarkAges.exe");
        var base = path.getParent().resolve("base");

        var spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        dumpStreams(spec, base);
    }

    private static void dumpStreams(PackageMapSpec spec, Path base) throws Exception {
        var resourcePaths = spec.files().stream()
            .filter(f -> f.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        var names = new ArrayList<String>();
        var entries = new ArrayList<StreamDbEntry>();
        for (Path path : resourcePaths) {
            try (var source = DataSource.fromPath(path)) {
                StreamDb streamDb = StreamDb.read(source);
                var sortedEntries = streamDb.entries().stream()
                    .sorted(Comparator.comparing(StreamDbEntry::offset))
                    .toList();

                var name = path.getFileName().toString();
                for (int i = 0; i < sortedEntries.size(); i++) {
                    names.add(name);
                }
                entries.addAll(sortedEntries);
            }
        }

        var csv = CsvUtils.toCsv(names, entries, StreamDbEntry.class);
        Files.writeString(Path.of("D:\\Projects\\DarkAges\\CSV\\streamdb_entries.csv"), csv);
    }

    private static void dumpResources(PackageMapSpec spec, Path base) throws Exception {
        var resourcePaths = spec.files().stream()
            .filter(f -> f.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        var names = new ArrayList<String>();
        var entries = new ArrayList<ImageHeader>();
        for (Path path : resourcePaths) {
            try (ResourcesFile resourcesFile = new ResourcesFile(path, DECOMPRESSOR)) {
                resourcesFile.getAll().forEach(entry -> {
                    if (entry.size() == 0 || entry.id().type() != ResourcesType.Image) {
                        return;
                    }
                    try {
                        ByteBuffer buffer = resourcesFile.read(entry.id(), null);
                        try (var source = DataSource.fromBuffer(buffer)) {
                            var header = ImageHeader.read(source);
                            names.add(entry.id().fullName());
                            entries.add(header);
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }

        var csv = CsvUtils.toCsv(names, entries, ImageHeader.class);
        Files.writeString(Path.of("D:\\Projects\\DarkAges\\CSV\\image_headers.csv"), csv);
    }

    private static void extractAll(PackageMapSpec spec, Path base) throws Exception {
        var resourcePaths = spec.files().stream()
            .filter(f -> f.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        List<ResourcesEntry> entries = new ArrayList<>();
        Path outBase = Path.of("D:\\Projects\\DarkAges\\Extracted");
        for (Path path : resourcePaths) {
            try (ResourcesFile resourcesFile = new ResourcesFile(path, DECOMPRESSOR)) {
                resourcesFile.getAll().forEach(entry -> {
                    if (entry.size() == 0) {
                        return;
                    }
                    try {
                        var out = outBase
                            .resolve(entry.id().type().value())
                            .resolve(entry.id().pathName())
                            .resolve(entry.id().fileName() + "." + entry.id().type().value());

                        ByteBuffer buffer = resourcesFile.read(entry.id(), null);
                        if (!Files.exists(out)) {
                            Files.createDirectories(out.getParent());
                            Files.write(out, Buffers.toArray(buffer));
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }

        var csv = CsvUtils.toCsv(List.of(), entries, ResourcesEntry.class);
        Files.writeString(Path.of("D:\\Projects\\DarkAges\\CSV\\resource_entries.csv"), csv);
    }
}
