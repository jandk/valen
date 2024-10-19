package org.redeye.valen.game.spacemarines2.types.template;

import org.redeye.valen.game.spacemarines2.fio.*;

import java.util.*;

public class TplExternDataSerializer extends FioStructSerializer<TplExternData> {
    public TplExternDataSerializer() {
        super(TplExternData::new, List.of(
            new FioStructMember<>("Array", TplExternData::setArray, new FioArraySerializer<>(TplExternDataData::new, new TplExternDataDataSerializer()))
        ));
    }
}
