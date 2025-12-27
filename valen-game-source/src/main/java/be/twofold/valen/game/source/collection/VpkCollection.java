package be.twofold.valen.game.source.collection;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.source.*;
import be.twofold.valen.game.source.readers.vpk.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class VpkCollection implements Container<SourceAssetID, SourceAsset> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VpkCollection.class);

    private final BinarySource source;
    private final List<BinarySource> sources;
    private final Map<SourceAssetID, SourceAsset> index;

    public VpkCollection(Path path) throws IOException {
        LOGGER.info("Loading VPK directory from {}", path);

        this.source = BinarySource.open(path);
        VpkDirectory directory = VpkDirectory.read(source);

        this.index = directory.entries().stream()
            .map(entry -> new SourceAsset.Vpk(new SourceAssetID(entry.name()), entry))
            .collect(Collectors.toUnmodifiableMap(SourceAsset::id, Function.identity()));

        int maxArchiveIndex = directory.entries().stream()
            .mapToInt(VpkEntry::archiveIndex)
            .filter(value -> value != 0x7FFF)
            .max().orElseThrow();

        String template = path.getFileName().toString().replace("_dir.vpk", "");
        var sources = new ArrayList<BinarySource>(maxArchiveIndex + 1);
        for (int i = 0; i <= maxArchiveIndex; i++) {
            Path vpkPath = path.getParent().resolve(String.format("%s_%03d.vpk", template, i));
            LOGGER.info("  Loading {}", vpkPath);
            sources.add(BinarySource.open(vpkPath));
        }
        this.sources = List.copyOf(sources);
    }

    @Override
    public Optional<SourceAsset> get(SourceAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<SourceAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public Bytes read(SourceAssetID key, Integer size) throws IOException {
        var resource = index.get(key);
        Check.state(resource != null, () -> "Resource not found: " + key.name());
        if (!(resource instanceof SourceAsset.Vpk vpk)) {
            throw new IllegalArgumentException("Resource is not a VPK: " + key.name());
        }

        var bytes = Bytes.Mutable.allocate(vpk.size());
        vpk.entry().preloadBytes().copyTo(bytes, 0);

        var source = vpk.entry().archiveIndex() != 0x7FFF
            ? sources.get(vpk.entry().archiveIndex())
            : this.source;
        source.position(vpk.entry().entryOffset());
        source.readBytes(bytes.slice(vpk.entry().preloadBytes().length(), vpk.entry().entryLength()));
        return bytes;
    }

    @Override
    public void close() throws IOException {
        source.close();
        for (BinarySource source : sources) {
            source.close();
        }
    }
}
