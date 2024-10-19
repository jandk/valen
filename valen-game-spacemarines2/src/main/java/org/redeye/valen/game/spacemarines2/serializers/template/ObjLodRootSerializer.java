package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjLodRootSerializer extends FioStructSerializer<ObjLodRoot> {
    public ObjLodRootSerializer() {
        super(ObjLodRoot::new, List.of(
            new FioStructMember<>("ObjIds", ObjLodRoot::setObjIds, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("MaxObjLodIndices", ObjLodRoot::setMaxObjLodIndices, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("LodDists", ObjLodRoot::setLodDists, new FioArraySerializer<>(ObjLodDist::new, new ObjLodDistSerializer())),
            new FioStructMember<>("Bbox", ObjLodRoot::setBbox, new BBoxSerializer()),
            new FioStructMember<>("Skip float", (objLodRoot, aFloat) -> {
            }, new FioFloatSerializer()),
            new FioStructMember<>("DontApplyBias", ObjLodRoot::setDontApplyBias, new FioInt8Serializer())

        ));
    }
}
