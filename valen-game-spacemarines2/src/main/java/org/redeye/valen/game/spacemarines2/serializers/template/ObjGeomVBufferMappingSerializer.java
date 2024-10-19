package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjGeomVBufferMappingSerializer extends FioStructSerializer<ObjGeomVBufferMapping> {
    public ObjGeomVBufferMappingSerializer() {
        super(ObjGeomVBufferMapping::new, List.of(
            new FioStructMember<>("StreamToVBuffer", ObjGeomVBufferMapping::setStreamToVBuffer, new FioArraySerializer<>(ObjGeomStreamToVBuffer::new, new ObjGeomStreamToVBufferSerializer())),
            new FioStructMember<>("vBufferInfo", ObjGeomVBufferMapping::setvBufferInfo, new FioArraySerializer<>(ObjGeomVBufferInfo::new, new ObjGeomVBufferInfoSerializer()))
        ));
    }
}
