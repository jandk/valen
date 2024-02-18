package be.twofold.valen.reader;

import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.resource.*;

public interface ResourceReader<R> {
    R read(BetterBuffer buffer, Resource resource, FileManager manager);
}
