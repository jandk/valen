package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ObjPropStorageSerializer extends FioStructSerializer<ObjPropStorage> {
    public ObjPropStorageSerializer() {
        super(ObjPropStorage::new, 12, List.of(
            new FioStructMember<>("ObjProps", ObjPropStorage::setObjProps, new ObjPropsSerializer())
        ));
    }
}
