package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjSplitRangeSerializer extends FioStructSerializer<ObjSplitRange> {
    public ObjSplitRangeSerializer() {
        super(ObjSplitRange::new, List.of(
            new FioStructMember<>("StartIndex", ObjSplitRange::setStartIndex, new FioInt16Serializer()),
            new FioStructMember<>("NumSplits", ObjSplitRange::setNumSplits, new FioInt16Serializer())
        ));
    }
}
