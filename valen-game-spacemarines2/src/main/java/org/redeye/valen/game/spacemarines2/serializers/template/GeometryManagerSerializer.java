package org.redeye.valen.game.spacemarines2.serializers.template;

import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class GeometryManagerSerializer extends FioStructSerializer<GeometryManager> {
    public static final int SIGNATURE = 'O' | 'G' << 8 | 'M' << 16 | '1' << 24; // TPL1

    public GeometryManagerSerializer() {
        super(SIGNATURE, GeometryManager::new, 12, List.of(
            new FioStructMember<>("Objects", GeometryManager::setObjects, new FioArraySerializer<>(ObjObj::new, 9, new ObjObjSerializer())),
            new FioStructMember<>("ObjectProps", GeometryManager::setObjectProps, new FioArraySerializer<>(() -> new Pair<>(null, null), 0, new PairSerializer<>(new FioInt32Serializer(), new ObjPropStorageSerializer()))),
            new FioStructMember<>("ObjPs", GeometryManager::setObjPs, new FioArraySerializer<>(() -> new Pair<>(null, null), 0, new PairSerializer<>(new FioInt32Serializer(), new FioStringSerializer()))),
            new FioStructMember<>("LodRoots", GeometryManager::setLodRoots, new FioArraySerializer<>(ObjLodRoot::new, 9, new ObjLodRootSerializer())),
            new FioStructMember<>("vBufferMapping", GeometryManager::setvBufferMapping, new ObjGeomVBufferMappingSerializer()),
            new FioStructMember<>("NamedObjectsName", GeometryManager::setNamedObjectsName, new FioArraySerializer<>(() -> "", 9, new FioStringSerializer())),
            new FioStructMember<>("NamedObjectsId", GeometryManager::setNamedObjectsId, new FioArraySerializer<>(() -> (short) 0, 9, new FioInt16Serializer(16))),
            new FioStructMember<>("MtrLt", GeometryManager::setMtrLt, new FioArraySerializer<>(() -> Matrix4.Identity, 9, new MatrixSerializer(16))),
            new FioStructMember<>("MtrModel", GeometryManager::setMtrModel, new FioArraySerializer<>(() -> Matrix4.Identity, 9, new MatrixSerializer(16))),
            new FioStructMember<>("ObjSpitInfo", GeometryManager::setObjSpitInfo, new FioArraySerializer<>(ObjSplitRange::new, 9, new ObjSplitRangeSerializer()))
        ), GeometryManager::onReadFinishCallback);
    }
}
