package be.twofold.valen.game.darkages;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.reader.packagemapspec.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Experiment {
    public static void main(String[] args) throws Exception {
        var path = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOMTheDarkAges\\DOOMTheDarkAges.exe");
        var base = path.getParent().resolve("base");

        var spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        dumpResources(spec, base);
    }

    private static void dumpResources(PackageMapSpec spec, Path base) throws Exception {
        var resourcePaths = spec.files().stream()
            .filter(f -> f.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        Set<String> allTypes = new HashSet<>();
        List<ResourcesEntry> entries = new ArrayList<>();
        for (Path path : resourcePaths) {
            try (var source = DataSource.fromPath(path)) {
                Resources resources = Resources.read(source);
                var types = resources.entries().stream()
                    .map(e -> resources.pathStrings().get(resources.pathStringIndex()[e.strings() + 0]))
                    .toList();
                allTypes.addAll(types);
                entries.addAll(resources.entries());
            }
        }

        for (String type : allTypes) {
            System.out.println(type);
        }

        var csv = CsvUtils.toCsv(List.of(), entries, ResourcesEntry.class);
        Files.writeString(Path.of("D:\\Projects\\DarkAges\\CSV\\resource_entries.csv"), csv);
    }
}
