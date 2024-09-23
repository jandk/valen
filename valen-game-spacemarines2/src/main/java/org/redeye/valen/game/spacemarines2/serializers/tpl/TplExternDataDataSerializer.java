package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;

import java.util.*;

public class TplExternDataDataSerializer extends FioStructSerializer<TplExternDataData> {
    public TplExternDataDataSerializer() {
        super(TplExternDataData::new, 12, List.of(
            new FioStructMember<>("Name", TplExternDataData::setName, new FioStringSerializer()),
            new FioStructMember<>("Data", TplExternDataData::setData, new FioArraySerializer<>(() -> null, 9, new FioInt8Serializer(16)))

        ));
    }
}
