package be.twofold.valen.reader.md6skl;

import be.twofold.valen.core.util.*;

public final class Md6SkeletonReader {
    public Md6Skeleton read(BetterBuffer buffer) {
        return Md6Skeleton.read(buffer);
    }
}
