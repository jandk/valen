package be.twofold.valen.game.eternal.reader.file.mapresources;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.file.FileReader;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class MapResourcesReader implements AssetReader<MapResources, EternalAsset> {
    private final FileReader fileReader;

    public MapResourcesReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.File
            && resource.id().extension().equals("mapresources");
    }

    @Override
    public MapResources read(BinarySource source, EternalAsset resource, LoadingContext context) throws IOException {
        var bytes = fileReader.read(source, resource, context);
        return MapResources.read(BinarySource.wrap(bytes));
    }
}
