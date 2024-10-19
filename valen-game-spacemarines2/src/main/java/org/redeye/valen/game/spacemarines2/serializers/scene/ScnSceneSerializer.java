package org.redeye.valen.game.spacemarines2.serializers.scene;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.serializers.template.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.redeye.valen.game.spacemarines2.types.scene.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ScnSceneSerializer extends FioStructSerializer<ScnScene> {
    public static final int SIGNATURE = 'S' | 'C' << 8 | 'N' << 16 | '1' << 24; // TPL1

    public ScnSceneSerializer() {
        super(SIGNATURE, ScnScene::new, List.of(
            new FioStructMember<>("texList", ScnScene::setTexList, new TxmTexListSerializer()),
            new FioStructMember<>("psCache", ScnScene::setPsCache, new FioStringSerializer()),
            new FioStructMember<>("strPairs", ScnScene::setSetStrPairs, new FioArraySerializer<>(() -> new Pair<>(null, null), new PairSerializer<>(new FioStringSerializer(), new FioStringSerializer()))),
            new FioStructMember<>("null", (ignored, ignored2) -> {
            }, new NullSerializer()),
            new FioStructMember<>("geomManager", ScnScene::setGeomManager, new GeometryManagerSerializer()),
            new FioStructMember<>("ExternData", ScnScene::setExternData, new TplExternDataSerializer()),
            new FioStructMember<>("PColl", ScnScene::setpColl, new CdtCollSerializer()),
            new FioStructMember<>("BrandCache", ScnScene::setBrandCache, new ScnBrandCacheSerializer()),
            new FioStructMember<>("UnkInts", ScnScene::setUnkInts, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("State", ScnScene::setState, new FioBitSetFlagsSerializer())
        ));
    }
}
