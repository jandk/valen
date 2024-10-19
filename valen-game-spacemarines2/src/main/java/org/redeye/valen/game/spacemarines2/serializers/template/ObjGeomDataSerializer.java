package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjGeomDataSerializer extends FioStructSerializer<ObjGeomUnshared> {
    public ObjGeomDataSerializer() {
        super(ObjGeomUnshared::new, List.of(
            new FioStructMember<>("SplitIndex", ObjGeomUnshared::setSplitIndex, new FioInt32Serializer()),
            new FioStructMember<>("SplitCount", ObjGeomUnshared::setSplitCount, new FioInt32Serializer()),
            new FioStructMember<>("BBox", ObjGeomUnshared::setBbox, new BBoxSerializer()),
            new FioStructMember<>("OBB", ObjGeomUnshared::setObb, new ObbSerializer())
        ));
    }
}
