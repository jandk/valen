package be.twofold.valen.game.deathloop.master;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record MasterIndex(
    int magic,
    short version,
    String indexFile,
    List<String> dataFiles
) {
    public static MasterIndex read(Path path) throws IOException {
        try (var source = DataSource.fromPath(path)) {
            var magic = source.readIntBE();
            var version = source.readShortBE();
            var indexFile = source.readPString();
            var numDataFiles = source.readShortBE();
            var dataFiles = source.readStructs(numDataFiles, DataSource::readPString);
            return new MasterIndex(magic, version, indexFile, dataFiles);
        }
    }
}
