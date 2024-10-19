package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjGeomStreamToVBufferSerializer extends FioStructSerializer<ObjGeomStreamToVBuffer> {
    public ObjGeomStreamToVBufferSerializer() {
        super(ObjGeomStreamToVBuffer::new, List.of(
            new FioStructMember<>("vBufIdx,", ObjGeomStreamToVBuffer::setvBufIdx, new FioInt32Serializer()),
            new FioStructMember<>("vBufOffset", ObjGeomStreamToVBuffer::setvBufOffset, new FioInt32Serializer())
        ));
    }
}
