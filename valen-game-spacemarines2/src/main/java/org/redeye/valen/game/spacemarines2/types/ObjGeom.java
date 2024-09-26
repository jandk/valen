package org.redeye.valen.game.spacemarines2.types;

import java.util.*;

public class ObjGeom {
    public static final int STRM_TOTAL = GeomStreamSlot.OBJ_GEOM_STRM_TOTAL.value();

    public final Set<FVF> fvf = EnumSet.noneOf(FVF.class);
    public final Set<FVF> flags = EnumSet.noneOf(FVF.class);
    public final Map<GeomStreamSlot, ObjGeomStream> streams = new LinkedHashMap<>(STRM_TOTAL); // 6
    public final Map<GeomStreamSlot, Integer> streamsOffset = new LinkedHashMap<>(STRM_TOTAL); // 6
    public byte setId;
}
