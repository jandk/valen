package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjPropStorageSerializer extends FioStructSerializer<ObjPropStorage> {
    public ObjPropStorageSerializer() {
        super(ObjPropStorage::new, List.of(
            new FioStructMember<>("ObjProps", ObjPropStorage::setObjProps, new ObjPropsSerializer())
        ));
    }
}
