package be.twofold.valen.reader.file.mapresources;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.file.FileReader;
import be.twofold.valen.resource.*;

import java.io.*;

public final class MapResourcesReader implements ResourceReader<MapResources> {
    private final FileReader fileReader;

    public MapResourcesReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.File
            && key.name().extension().equals("mapresources");
    }

    @Override
    public MapResources read(DataSource source, Asset<ResourceKey> asset) throws IOException {
        var file = fileReader.read(source, asset);
        return MapResources.read(new ByteArrayDataSource(file.data()));
    }
}
