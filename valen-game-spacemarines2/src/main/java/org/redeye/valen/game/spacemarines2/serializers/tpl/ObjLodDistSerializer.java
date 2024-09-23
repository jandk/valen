package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ObjLodDistSerializer extends FioStructSerializer<ObjLodDist> {
    public ObjLodDistSerializer() {
        super(ObjLodDist::new, 12, List.of(
            new FioStructMember<>("Dist", ObjLodDist::setDist, new FioFloatSerializer())
        ));
    }
}
