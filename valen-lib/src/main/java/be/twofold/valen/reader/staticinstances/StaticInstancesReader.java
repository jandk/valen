package be.twofold.valen.reader.staticinstances;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.staticinstances.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

public class StaticInstancesReader implements ResourceReader<StaticInstances> {

    @Inject
    public StaticInstancesReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.StaticInstances;
    }

    @Override
    public StaticInstances read(BetterBuffer buffer, Resource resource) {
        return StaticInstances.read(buffer);
    }
}
