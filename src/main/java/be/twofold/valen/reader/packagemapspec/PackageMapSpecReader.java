package be.twofold.valen.reader.packagemapspec;

import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class PackageMapSpecReader {
    private final Reader reader;

    private List<String> files;
    private List<String> maps;

    private PackageMapSpecReader(Reader reader) {
        this.reader = reader;
    }

    public static PackageMapSpec read(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            return new PackageMapSpecReader(reader).read();
        } catch (IOException e) {
            System.err.println("Failed to read package map spec: " + path);
            throw new UncheckedIOException(e);
        }
    }

    private PackageMapSpec read() {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        files = readNames(root.get("files"));
        maps = readNames(root.get("maps"));
        Map<String, List<String>> mapFiles = readMapFiles(root.get("mapFileRefs"));
        return new PackageMapSpec(files, maps, mapFiles);
    }

    private List<String> readNames(JsonElement element) {
        return StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
            .map(e -> e.getAsJsonObject().get("name").getAsString())
            .toList();
    }

    private Map<String, List<String>> readMapFiles(JsonElement element) {
        Map<String, List<String>> mapFiles = new HashMap<>();
        for (JsonElement e : element.getAsJsonArray()) {
            String map = maps.get(e.getAsJsonObject().get("map").getAsInt());
            String file = files.get(e.getAsJsonObject().get("file").getAsInt());
            mapFiles.computeIfAbsent(map, __ -> new ArrayList<>()).add(file);
        }
        mapFiles.replaceAll((k, v) -> List.copyOf(v));
        return Map.copyOf(mapFiles);
    }
}
