package org.redeye.valen.game.spacemarines2;

import org.redeye.valen.game.spacemarines2.readers.*;

import java.util.*;

public class AllReaders {
    public static final List<Reader<?>> READERS = List.of(new ResourceReader(),
        new TDReader(),
        new TPLReader(),
        new LGReader(),
        new ClassListReader(),
        new TerrainReader(),
        new CdListReader(),
        new StaticInstanceDataReader(),
        new LwiContainerReader(),
        new PCTReader()
    );
}
