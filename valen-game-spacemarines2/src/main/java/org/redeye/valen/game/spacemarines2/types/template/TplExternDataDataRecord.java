package org.redeye.valen.game.spacemarines2.types.template;

import org.redeye.valen.game.spacemarines2.fio.*;

import java.util.*;

public record TplExternDataDataRecord(
    @FioField(serializer = FioStringSerializer.class)
    String name,

    @FioField(serializer = FioInt8Serializer.class)
    List<Byte> data

    // @FioField(serializer = FioStructSerializer.class)
    // TplSkin skin
) {
}
