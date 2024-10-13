package org.redeye.valen.game.spacemarines2.serializers;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class InteractiveItemSerializer extends FioStructSerializer<InteractiveItem> {
    public InteractiveItemSerializer() {
        super(InteractiveItem::new, 12, List.of(
            new FioStructMember<>("Key", InteractiveItem::setKey, new FioInt64Serializer()),
            new FioStructMember<>("BlockId", InteractiveItem::setBlockId, new FioInt32Serializer()),
            new FioStructMember<>("InstId", InteractiveItem::setInstId, new FioInt32Serializer())
        ));
    }
}
