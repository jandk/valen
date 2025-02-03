package be.twofold.valen.game.eternal.reader.file.mapresources;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.file.FileReader;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;

public final class MapResourcesReader implements AssetReader<MapResources, Resource> {
    private final FileReader fileReader;

    public MapResourcesReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @Override
    public boolean canRead(Resource resource) {
        return resource.key().type() == ResourceType.File
            && resource.key().name().extension().equals("mapresources");
    }

    @Override
    public MapResources read(DataSource source, Resource resource) throws IOException {
        var file = fileReader.read(source, resource);
        return MapResources.read(DataSource.fromArray(file.data()));
    }
}
