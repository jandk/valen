package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjGeomStreamSetRefSerializer extends FioStructSerializer<ObjGeomStreamSetRef> {
    public ObjGeomStreamSetRefSerializer() {
        super(ObjGeomStreamSetRef::new, List.of(
            new FioStructMember<>("StreamIds", ObjGeomStreamSetRef::setStreamIds, new FioArraySerializer<>(() -> 0, new FioInt32Serializer()))
        ));
    }
}
