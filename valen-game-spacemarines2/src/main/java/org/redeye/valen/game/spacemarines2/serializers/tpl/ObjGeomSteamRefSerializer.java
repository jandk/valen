package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ObjGeomSteamRefSerializer extends FioStructSerializer<ObjGeomSteamRef> {
    public ObjGeomSteamRefSerializer() {
        super(ObjGeomSteamRef::new, 12, List.of(
            new FioStructMember<>("Offset", ObjGeomSteamRef::setOffset, new FioInt64Serializer(16)),
            new FioStructMember<>("Size", ObjGeomSteamRef::setSize, new FioInt64Serializer(16))
        ));
    }
}