package be.twofold.valen.reader.blang;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;

public final class BlangReader implements ResourceReader<Blang> {
    @Override
    public Blang read(BetterBuffer buffer, Resource resource) {
        return Blang.read(buffer);
    }
}
