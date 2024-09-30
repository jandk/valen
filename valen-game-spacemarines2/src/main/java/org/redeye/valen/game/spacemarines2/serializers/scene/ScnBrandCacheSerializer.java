package org.redeye.valen.game.spacemarines2.serializers.scene;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.scene.*;

import java.util.*;

public class ScnBrandCacheSerializer extends FioStructSerializer<ScnBrandCache> {

    public ScnBrandCacheSerializer() {
        super(ScnBrandCache::new, 12, List.of(
            new FioStructMember<>("ListBrandId", ScnBrandCache::setListBrandId, new FioArraySerializer<>(ScnBrandCacheInfo::new, 9, new ScnBrandCacheInfoSerializer()))
        ));
    }
}
