package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ObjGeomVBufferInfoSerializer extends FioStructSerializer<ObjGeomVBufferInfo> {
    public ObjGeomVBufferInfoSerializer() {
        super(ObjGeomVBufferInfo::new, 12, List.of(
            new FioStructMember<>("Size,", ObjGeomVBufferInfo::setSize, new FioInt32Serializer()),
            new FioStructMember<>("Flags", (handler, value) -> handler.setFlags(BitSet.valueOf(new byte[]{(byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF)})), new FioInt16Serializer()),
            new FioStructMember<>("Flags", ObjGeomVBufferInfo::setFlags, new FioBitSetFlagsSerializer())
        ));
    }
}
