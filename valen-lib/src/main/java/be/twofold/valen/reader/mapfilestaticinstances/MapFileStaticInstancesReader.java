package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;

public final class MapFileStaticInstancesReader implements ResourceReader<MapFileStaticInstances> {
    @Inject
    public MapFileStaticInstancesReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.StaticInstances;
    }

    @Override
    public MapFileStaticInstances read(DataSource source, Resource resource) throws IOException {
        return MapFileStaticInstances.read(source);
    }
}
