package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjLodDistSerializer extends FioStructSerializer<ObjLodDist> {
    public ObjLodDistSerializer() {
        super(ObjLodDist::new, 12, List.of(
            new FioStructMember<>("Dist", ObjLodDist::setDist, new FioFloatSerializer())
        ));
    }
}
