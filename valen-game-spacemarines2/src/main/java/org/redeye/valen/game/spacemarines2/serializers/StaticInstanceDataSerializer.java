package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class StaticInstanceDataSerializer extends FioStructSerializer<StaticInstanceData> {
    public StaticInstanceDataSerializer() {
        super(StaticInstanceData::new, List.of(
            new FioStructMember<>("SceneGuid", StaticInstanceData::setSceneGuid, new FioUUIDSerializer()),
            new FioStructMember<>("StaticInstAABB", StaticInstanceData::setStaticInstAABB, new BBoxSerializer()),
            new FioStructMember<>("InteractiveItemsList", StaticInstanceData::setInteractiveItemsList, new FioArraySerializer<>(InteractiveItem::new, new InteractiveItemSerializer())),
            new FioStructMember<>("VisBlockData", StaticInstanceData::setVisBlockData, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("VisBlockPlacementData", StaticInstanceData::setVisBlockPlacementData, new FioArraySerializer<>(() -> new Vector4(0, 0, 0, 1), new IntVec4Serializer())),
            new FioStructMember<>("InstBlockData", StaticInstanceData::setInstBlockData, new FioArraySerializer<>(BlockInfo::new, new BlockInfoSerializer())),
            new FioStructMember<>("StaticInstData", StaticInstanceData::setStaticInstData, new FioArraySerializer<>(InstanceDataStatic::new, new InstanceDataStaticSerializer())),
            new FioStructMember<>("VisBlockObbData", StaticInstanceData::setVisBlockObbData, new FioArraySerializer<>(VisBlockOBBData::new, new VisBlockOBBDataSerializer()))
        ));
    }
}
