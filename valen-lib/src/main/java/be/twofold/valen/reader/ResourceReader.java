package be.twofold.valen.reader;

import be.twofold.valen.core.util.*;
import be.twofold.valen.resource.*;

public interface ResourceReader<R> {

    boolean canRead(Resource entry);

    R read(BetterBuffer buffer, Resource resource);

}
