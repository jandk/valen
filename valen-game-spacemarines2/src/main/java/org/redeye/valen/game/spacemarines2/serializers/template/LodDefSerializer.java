package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class LodDefSerializer extends FioStructSerializer<LodDef> {
    public LodDefSerializer() {
        super(LodDef::new, 12, List.of(
            new FioStructMember<>("ObjId", LodDef::setObjId, new FioInt16Serializer()),
            new FioStructMember<>("Index", LodDef::setIndex, new FioInt8Serializer()),
            new FioStructMember<>("IsLastLodUpToInfinity", LodDef::setIsLastLodUpToInfinity, new FioInt8Serializer())
        ));
    }
}
