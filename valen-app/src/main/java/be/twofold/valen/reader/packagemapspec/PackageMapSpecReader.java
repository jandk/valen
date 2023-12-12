package be.twofold.valen.reader.packagemapspec;

import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class PackageMapSpecReader {
    public static PackageMapSpec read(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            return read(reader);
        } catch (IOException e) {
            System.err.println("Failed to read package map spec: " + path);
            throw new UncheckedIOException(e);
        }
    }

    private static PackageMapSpec read(Reader reader) {
        var root = JsonParser
            .parseReader(reader)
            .getAsJsonObject();

        return mapSchema(root);
    }

    private static PackageMapSpec mapSchema(JsonObject root) {
        var files = mapNames(root.getAsJsonArray("files"));
        var maps = mapNames(root.getAsJsonArray("maps"));

        Map<String, List<String>> mapFiles = new HashMap<>();
        for (var element : root.getAsJsonArray("mapFileRefs")) {
            var file = element.getAsJsonObject().get("file").getAsInt();
            var map = element.getAsJsonObject().get("map").getAsInt();
            mapFiles
                .computeIfAbsent(maps.get(map), __ -> new ArrayList<>())
                .add(files.get(file));
        }
        mapFiles.replaceAll((k, v) -> List.copyOf(v));
        return new PackageMapSpec(files, maps, Map.copyOf(mapFiles));
    }

    private static List<String> mapNames(JsonArray array) {
        return StreamSupport.stream(array.spliterator(), false)
            .map(e -> e.getAsJsonObject().get("name").getAsString())
            .toList();
    }
}
