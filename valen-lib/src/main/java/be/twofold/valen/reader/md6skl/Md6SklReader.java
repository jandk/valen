package be.twofold.valen.reader.md6skl;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

public final class Md6SklReader implements ResourceReader<Skeleton> {
    @Inject
    public Md6SklReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.Skeleton;
    }

    @Override
    public Skeleton read(BetterBuffer buffer, Resource resource) {
        Md6Skl skeleton = Md6Skl.read(buffer);
        return Md6SklMapper.map(skeleton);
    }
}
