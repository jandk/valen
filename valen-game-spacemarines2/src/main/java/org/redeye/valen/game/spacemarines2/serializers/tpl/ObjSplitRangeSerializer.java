package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ObjSplitRangeSerializer extends FioStructSerializer<ObjSplitRange> {
    public ObjSplitRangeSerializer() {
        super(ObjSplitRange::new, 12, List.of(
            new FioStructMember<>("StartIndex", ObjSplitRange::setStartIndex, new FioInt16Serializer(16)),
            new FioStructMember<>("NumSplits", ObjSplitRange::setNumSplits, new FioInt16Serializer(16))
        ));
    }
}
