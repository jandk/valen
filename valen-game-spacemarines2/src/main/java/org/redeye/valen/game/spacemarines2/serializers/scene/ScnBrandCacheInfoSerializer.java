package org.redeye.valen.game.spacemarines2.serializers.scene;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.scene.*;

import java.util.*;

public class ScnBrandCacheInfoSerializer extends FioStructSerializer<ScnBrandCacheInfo> {
    public ScnBrandCacheInfoSerializer() {
        super(ScnBrandCacheInfo::new, 12, List.of(
            new FioStructMember<>("ParentDesc", ScnBrandCacheInfo::setParentDesc, new FioStringSerializer()),
            new FioStructMember<>("Tpl", ScnBrandCacheInfo::setTpl, new FioStringSerializer()),
            new FioStructMember<>("PsId", ScnBrandCacheInfo::setPsId, new FioInt16Serializer())
        ));
    }
}
