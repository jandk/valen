package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;

import java.util.*;

public class TplExternDataSerializer extends FioStructSerializer<TplExternData> {
    public TplExternDataSerializer() {
        super(TplExternData::new, 12, List.of(
            new FioStructMember<>("Array", TplExternData::setArray, new FioArraySerializer<>(TplExternDataData::new, 9, new TplExternDataDataSerializer()))
        ));
    }
}
