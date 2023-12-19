package be.twofold.valen.reader.md6skl;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;

public final class Md6SkeletonReader implements ResourceReader<Skeleton> {
    @Override
    public Skeleton read(BetterBuffer buffer) {
        Md6Skeleton skeleton = Md6Skeleton.read(buffer);
        return Md6SkeletonMapper.map(skeleton);
    }
}
