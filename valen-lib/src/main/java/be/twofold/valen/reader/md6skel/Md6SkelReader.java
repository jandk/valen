package be.twofold.valen.reader.md6skel;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

public final class Md6SkelReader implements ResourceReader<Skeleton> {
    @Inject
    public Md6SkelReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.Skeleton;
    }

    @Override
    public Skeleton read(BetterBuffer buffer, Resource resource) {
        Md6Skel skeleton = Md6Skel.read(buffer);
        return Md6SkelMapper.map(skeleton);
    }
}
