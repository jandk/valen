package be.twofold.valen.game.source.index;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.source.*;
import be.twofold.valen.game.source.readers.vpk.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record VpkIndex(
    Map<FileId, BinarySource> sources,
    Map<SourceAssetID, SourceAsset> index
) {
    private static final Logger LOGGER = LoggerFactory.getLogger(VpkIndex.class);
    private static final DecimalFormat FORMAT = new DecimalFormat("000");

    public VpkIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    public static VpkIndex build(Path path) throws IOException {
        LOGGER.info("Loading VPK directory from {}", path);

        var source = BinarySource.open(path);
        var directory = VpkDirectory.read(source);

        var maxArchiveIndex = directory.entries().stream()
            .mapToInt(VpkEntry::archiveIndex)
            .filter(value -> value != 0x7FFF)
            .max().orElseThrow();

        var template = path.getFileName().toString().replace("_dir.vpk", "");
        var fileIds = new ArrayList<FileId>();
        for (int i = 0; i <= maxArchiveIndex; i++) {
            var fileId = new FileId(template + "_" + FORMAT.format(i) + ".vpk");
            fileIds.add(fileId);
        }

        var rootFileId = new FileId(path.getFileName().toString());
        var sources = new HashMap<FileId, BinarySource>();
        sources.put(rootFileId, source);
        for (var fileId : fileIds) {
            var vpkPath = path.getParent().resolve(fileId.name());
            LOGGER.info("  Loading {}", vpkPath);
            sources.put(new FileId(fileId.name()), BinarySource.open(vpkPath));
        }

        var index = directory.entries().stream()
            .map(entry -> mapEntry(entry, entry.archiveIndex() == 0x7FFF ? rootFileId : fileIds.get(entry.archiveIndex())))
            .collect(Collectors.toUnmodifiableMap(SourceAsset::id, Function.identity()));

        return new VpkIndex(sources, index);
    }

    private static SourceAsset mapEntry(VpkEntry entry, FileId fileId) {
        boolean hasPreload = entry.preloadBytes().length() > 0;
        boolean hasEntry = entry.entryLength() > 0;
        if (hasPreload && hasEntry) {
            throw new IllegalStateException("VPK entry " + entry.name() + " has both preload and entry data");
        }

        Location location = hasEntry
            ? new Location.FileSlice(fileId, entry.entryOffset(), entry.entryLength())
            : new Location.InMemory(entry.preloadBytes());

        return new SourceAsset(new SourceAssetID(entry.name()), location);
    }
}
