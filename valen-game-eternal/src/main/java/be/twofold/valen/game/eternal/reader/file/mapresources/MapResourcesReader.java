package be.twofold.valen.game.eternal.reader.file.mapresources;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.file.FileReader;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;

public final class MapResourcesReader implements AssetReader.Binary<MapResources, EternalAsset> {
    private final FileReader fileReader;

    public MapResourcesReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.File
            && asset.id().extension().equals("mapresources");
    }

    @Override
    public MapResources read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        var bytes = fileReader.read(source, asset, context);
        return MapResources.read(BinarySource.wrap(bytes));
    }

    @Override
    public Optional<Meta.Node> readMetadata(EternalAsset asset, LoadingContext context) throws IOException {
        try (var source = BinarySource.wrap(context.open(asset.location()))) {
            var bytes = fileReader.read(source, asset, context);
            var mapResources = MapResources.read(BinarySource.wrap(bytes));
            return Optional.of(Meta.build(MethodHandles.lookup(), mapResources));
        }
    }
}
