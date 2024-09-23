package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ObjGeomStreamSetRefSerializer extends FioStructSerializer<ObjGeomStreamSetRef> {
    public ObjGeomStreamSetRefSerializer() {
        super(ObjGeomStreamSetRef::new, 12, List.of(
            new FioStructMember<>("StreamIds", ObjGeomStreamSetRef::setStreamIds, new FioArraySerializer<>(() -> 0, 9, new FioInt32Serializer(16)))
        ));
    }
}
