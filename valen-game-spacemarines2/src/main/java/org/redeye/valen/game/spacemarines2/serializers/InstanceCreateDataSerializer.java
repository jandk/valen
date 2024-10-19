package org.redeye.valen.game.spacemarines2.serializers;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.scene.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class InstanceCreateDataSerializer extends FioStructSerializer<SceneInstanceCreateData> {
    public InstanceCreateDataSerializer() {
        super(SceneInstanceCreateData::new, List.of(
            new FioStructMember<>("Name", SceneInstanceCreateData::setName, new FioStringSerializer()),
            new FioStructMember<>("Name2", SceneInstanceCreateData::setName2, new FioStringSerializer()),
            new FioStructMember<>("Mat", SceneInstanceCreateData::setMat, new MatrixSerializer()),
            new FioStructMember<>("Affixes", SceneInstanceCreateData::setAffixes, new FioStringSerializer()),
            new FioStructMember<>("Ps", SceneInstanceCreateData::setPs, new CachedPsSerializer()),
            new FioStructMember<>("unkShort", (ignored_, ignored) -> {
            }, new FioInt16Serializer()),
            new FioStructMember<>("Overrides", SceneInstanceCreateData::setOverrides, new FioArraySerializer<>(ScnInstanceOverrideData::new, new ScnInstanceOverrideDataSerializer())),
            new FioStructMember<>("ParentInstIdx", SceneInstanceCreateData::setParentInstIdx, new FioInt32Serializer()),
            new FioStructMember<>("ParentObj", SceneInstanceCreateData::setParentObj, new FioStringSerializer()),
            new FioStructMember<>("GameObjectFlags", (obj, b) -> {
                obj.setGameObjectFlags(BitSet.valueOf(new byte[]{b}));
            }, new FioInt8Serializer())
        ));
    }
}
