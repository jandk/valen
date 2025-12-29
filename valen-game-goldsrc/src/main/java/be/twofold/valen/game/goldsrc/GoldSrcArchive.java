package be.twofold.valen.game.goldsrc;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.goldsrc.container.*;
import be.twofold.valen.game.goldsrc.reader.mdl.*;
import be.twofold.valen.game.goldsrc.reader.wad.font.*;
import be.twofold.valen.game.goldsrc.reader.wad.miptex.*;
import be.twofold.valen.game.goldsrc.reader.wad.qpic.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class GoldSrcArchive extends Archive<GoldSrcAssetID, GoldSrcAsset> {
    private final FileContainer fileContainer;
    private final Map<String, WadContainer> wadContainers;

    public GoldSrcArchive(Path path) throws IOException {
        this.fileContainer = new FileContainer(path);
        var wadAssets = this.fileContainer.getAll()
            .filter(asset -> asset.id().baseName().endsWith(".wad"))
            .toList();

        var wadContainers = new HashMap<String, WadContainer>();
        for (GoldSrcAsset wadAsset : wadAssets) {
            String baseName = wadAsset.id().baseName();
            wadContainers.put(baseName, new WadContainer(path.resolve(baseName), baseName));
        }
        this.wadContainers = Map.copyOf(wadContainers);
    }

    @Override
    public List<AssetReader<?, GoldSrcAsset>> createReaders() {
        return List.of(
            new MdlReader(),
            new WadFontReader(),
            new WadMipTexReader(),
            new WadQpicReader()
        );
    }

    @Override
    public Optional<GoldSrcAsset> get(GoldSrcAssetID key) {
        if (key.isWad()) {
            return wadContainers
                .get(key.baseName())
                .get(key);
        }
        return fileContainer.get(key);
    }

    @Override
    public Stream<GoldSrcAsset> getAll() {
        return Stream.concat(
            fileContainer.getAll().filter(asset -> !asset.id().baseName().endsWith(".wad")),
            wadContainers.values().stream().flatMap(WadContainer::getAll)
        );
    }

    @Override
    public Bytes read(GoldSrcAssetID key, Integer size) throws IOException {
        if (key.isWad()) {
            return wadContainers
                .get(key.baseName())
                .read(key, size);
        }
        return fileContainer.read(key, size);
    }

    @Override
    public void close() throws IOException {
        fileContainer.close();
        for (var wadContainer : wadContainers.values()) {
            wadContainer.close();
        }
    }
}
