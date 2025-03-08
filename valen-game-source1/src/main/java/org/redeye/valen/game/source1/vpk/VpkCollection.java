package org.redeye.valen.game.source1.vpk;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.source1.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class VpkCollection implements Container<SourceAssetID, SourceAsset> {
    private final List<DataSource> sources = new ArrayList<>();
    private final VpkDirectory directory;
    private final Map<String, VpkEntry> index;

    public VpkCollection(Path path) throws IOException {
        try (DataSource source = DataSource.fromPath(path)) {
            this.directory = VpkDirectory.read(source);
        }

        this.index = directory.entries().stream()
            .collect(Collectors.toUnmodifiableMap(VpkEntry::name, Function.identity()));

        int maxArchiveIndex = directory.entries().stream()
            .mapToInt(VpkEntry::archiveIndex)
            .max().orElseThrow();

        String template = path.getFileName().toString().replace("_dir.vpk", "");
        for (int i = 0; i < maxArchiveIndex; i++) {
            Path vpkPath = path.getParent().resolve(String.format("%s_%03d.vpk", template, i));
            sources.add(DataSource.fromPath(vpkPath));
        }
    }

    @Override
    public Optional<SourceAsset> get(SourceAssetID key) {
        return Optional.empty();
    }

    @Override
    public Stream<SourceAsset> getAll() {
        return Stream.empty();
    }

    @Override
    public ByteBuffer read(SourceAssetID key, int uncompressedSize) throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {
        for (DataSource source : sources) {
            source.close();
        }
    }
}
