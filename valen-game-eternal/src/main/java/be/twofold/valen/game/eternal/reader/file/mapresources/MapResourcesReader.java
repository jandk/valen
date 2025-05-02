package be.twofold.valen.game.eternal.reader.file.mapresources;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.file.*;
import be.twofold.valen.game.eternal.reader.file.FileReader;
import be.twofold.valen.game.eternal.resource.*;

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
    public MapResources read(DataSource source, EternalAsset resource) throws IOException {
        var buffer = fileReader.read(source, resource);
        return MapResources.read(DataSource.fromBuffer(buffer));
    }
}
