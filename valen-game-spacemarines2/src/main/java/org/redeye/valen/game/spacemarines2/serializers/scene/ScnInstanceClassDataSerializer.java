package org.redeye.valen.game.spacemarines2.serializers.scene;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ScnInstanceClassDataSerializer extends FioStructSerializer<ScnInstanceClassData> {
    public ScnInstanceClassDataSerializer() {
        super(ScnInstanceClassData::new, 12, List.of(
            new FioStructMember<>("Name", ScnInstanceClassData::setName, new FioStringSerializer()),
            new FioStructMember<>("Ps", ScnInstanceClassData::setPs, new FioPsSectionSerializer())
        ));
    }
}
