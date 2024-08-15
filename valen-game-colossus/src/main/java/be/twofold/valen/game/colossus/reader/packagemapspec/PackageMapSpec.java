package be.twofold.valen.game.colossus.reader.packagemapspec;

import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record PackageMapSpec(
    List<PackageMapSpecMap> maps,
    int patchLevel
) {
    public PackageMapSpec {
        maps = List.copyOf(maps);
    }

    public static PackageMapSpec read(Path path) throws IOException {
        var json = JsonParser
            .parseString(Files.readString(path))
            .getAsJsonObject();

        var maps = new ArrayList<PackageMapSpecMap>();
        for (var element : json.getAsJsonArray("maps")) {
            maps.add(PackageMapSpecMap.read(element.getAsJsonObject()));
        }

        var patchLevel = json.getAsJsonObject("patchinfo").get("patchlevel").getAsInt();

        return new PackageMapSpec(
            maps,
            patchLevel
        );
    }
}
