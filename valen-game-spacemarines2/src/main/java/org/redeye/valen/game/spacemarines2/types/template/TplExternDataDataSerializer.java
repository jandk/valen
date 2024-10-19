package org.redeye.valen.game.spacemarines2.types.template;

import org.redeye.valen.game.spacemarines2.fio.*;

import java.util.*;

public class TplExternDataDataSerializer extends FioStructSerializer<TplExternDataData> {
    public TplExternDataDataSerializer() {
        super(TplExternDataData::new, List.of(
            new FioStructMember<>("Name", TplExternDataData::setName, new FioStringSerializer()),
            new FioStructMember<>("Data", TplExternDataData::setData, new FioArraySerializer<>(() -> null, new FioInt8Serializer()))

        ));
    }
}
