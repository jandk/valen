package org.redeye.valen.game.spacemarines2.serializers;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class InstanceDataStaticSerializer extends FioStructSerializer<InstanceDataStatic> {
    public InstanceDataStaticSerializer() {
        super(InstanceDataStatic::new, 12, List.of(
            new FioStructMember<>("Data", InstanceDataStatic::setData, new Vec4Serializer())
        ));
    }
}
