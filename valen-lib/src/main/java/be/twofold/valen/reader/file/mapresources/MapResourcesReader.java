package be.twofold.valen.reader.file.mapresources;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.file.*;
import be.twofold.valen.resource.*;

public final class MapResourcesReader implements ResourceReader<MapResources> {
    private final FileReader fileReader = new FileReader();

    @Override
    public MapResources read(BetterBuffer buffer, Resource resource) {
        var file = fileReader.read(buffer, resource);
        return MapResources.read(BetterBuffer.wrap(file.data()));
    }
}
