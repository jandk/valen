package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class StaticInstanceDataSerializer extends FioStructSerializer<StaticInstanceData> {
    public StaticInstanceDataSerializer() {
        super(StaticInstanceData::new, 12, List.of(
            new FioStructMember<>("SceneGuid", StaticInstanceData::setSceneGuid, new FioUUIDSerializer()),
            new FioStructMember<>("StaticInstAABB", StaticInstanceData::setStaticInstAABB, new BoxSerializer()),
            new FioStructMember<>("InteractiveItemsList", StaticInstanceData::setInteractiveItemsList, new FioArraySerializer<>(InteractiveItem::new, 9, new InteractiveItemSerializer())),
            new FioStructMember<>("VisBlockData", StaticInstanceData::setVisBlockData, new FioArraySerializer<>(() -> 0, 9, new FioInt32Serializer())),
            new FioStructMember<>("VisBlockPlacementData", StaticInstanceData::setVisBlockPlacementData, new FioArraySerializer<>(() -> new Vector4(0, 0, 0, 1), 9, new IntVec4Serializer())),
            new FioStructMember<>("InstBlockData", StaticInstanceData::setInstBlockData, new FioArraySerializer<>(BlockInfo::new, 9, new BlockInfoSerializer())),
            new FioStructMember<>("StaticInstData", StaticInstanceData::setStaticInstData, new FioArraySerializer<>(InstanceDataStatic::new, 9, new InstanceDataStaticSerializer())),
            new FioStructMember<>("VisBlockObbData", StaticInstanceData::setVisBlockObbData, new FioArraySerializer<>(VisBlockOBBData::new, 9, new VisBlockOBBDataSerializer()))
        ));
    }
}
