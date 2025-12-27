package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.export.dds.*;
import be.twofold.valen.game.gustav.pak.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class GustavArchive extends Archive<GustavAssetID, GustavAsset> {
    private final PakFile pakFile;

    public GustavArchive(Path path) throws IOException {
        this.pakFile = new PakFile(path);
    }

    @Override
    public List<AssetReader<?, GustavAsset>> createReaders() {
        return List.of(
            DdsImporter.create()
        );
    }

    @Override
    public Optional<GustavAsset> get(GustavAssetID key) {
        return pakFile.get(key);
    }

    @Override
    public Stream<GustavAsset> getAll() {
        return pakFile.getAll();
    }

    @Override
    public Bytes read(GustavAssetID identifier, Integer size) throws IOException {
        return pakFile.read(identifier, null);
    }

    @Override
    public void close() throws IOException {
        pakFile.close();
    }
}
