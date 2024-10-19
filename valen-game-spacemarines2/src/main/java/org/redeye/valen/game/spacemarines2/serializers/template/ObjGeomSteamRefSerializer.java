package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjGeomSteamRefSerializer extends FioStructSerializer<ObjGeomStreamRef> {
    public ObjGeomSteamRefSerializer() {
        super(ObjGeomStreamRef::new, List.of(
            new FioStructMember<>("Offset", ObjGeomStreamRef::setOffset, new FioInt64Serializer()),
            new FioStructMember<>("Size", ObjGeomStreamRef::setSize, new FioInt64Serializer())
        ));
    }
}