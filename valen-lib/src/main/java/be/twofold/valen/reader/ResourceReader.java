package be.twofold.valen.reader;

import be.twofold.valen.core.util.*;

public interface ResourceReader<R> {
    R read(BetterBuffer buffer);
}
