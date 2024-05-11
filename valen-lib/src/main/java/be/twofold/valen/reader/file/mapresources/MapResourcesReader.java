package be.twofold.valen.reader.file.mapresources;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.file.FileReader;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;

public final class MapResourcesReader implements ResourceReader<MapResources> {

    private final FileReader fileReader;

    @Inject
    public MapResourcesReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.File
               && entry.name().extension().equals("mapresources");
    }

    @Override
    public MapResources read(DataSource source, Resource resource) throws IOException {
        var file = fileReader.read(source, resource);
        return MapResources.read(BetterBuffer.wrap(file.data()));
    }

}
