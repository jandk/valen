package org.redeye.valen.game.spacemarines2.serializers.tpl;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;

public class ObjPropsSerializer implements FioSerializer<ObjProp[]> {
    @Override
    public ObjProp[] load(DataSource source) throws IOException {
        var count = source.readShort();
        ObjProp[] props = new ObjProp[count];
        for (int i = 0; i < count; i++) {
            var type = source.readByte();
            Check.index(type, 2);
            ObjProp prop;
            if (type == 0) {
                prop = new ObjPropFaceGenMeshSerializer().load(source);
            } else if (type == 1) {
                prop = new ObjPropBlendShapeAnimSerializer().load(source);
            } else {
                throw new IllegalStateException("Invalid type: " + type);
            }
            props[i] = prop;
        }
        return props;
    }

    @Override
    public int flags() {
        return 0;
    }
}
