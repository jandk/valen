package be.twofold.valen.game.greatcircle.reader.packagemapspec;

import com.google.gson.*;
import com.google.gson.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record PackageMapSpec(
    @SerializedName("containers") List<Container> containers,
    @SerializedName("files") List<File> files,
    @SerializedName("initial_chunk") int initialChunk,
    @SerializedName("maps") List<Map> maps,
    @SerializedName("project") String project,
    @SerializedName("text_languages") List<String> textLanguages,
    @SerializedName("vo_chunk") int voChunk,
    @SerializedName("vo_languages") List<String> voLanguages
) {
    public static PackageMapSpec read(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            return new Gson().fromJson(reader, PackageMapSpec.class);
        } catch (IOException e) {
            System.err.println("Failed to read package map spec: " + path);
            throw new UncheckedIOException(e);
        }
    }

    public PackageMapSpec {
        containers = List.copyOf(containers);
        files = List.copyOf(files);
        maps = List.copyOf(maps);
        Objects.requireNonNull(project);
        textLanguages = List.copyOf(textLanguages);
        voLanguages = List.copyOf(voLanguages);
    }
}
