package org.redeye.valen.game.spacemarines2.serializers.scene;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ScnInstanceOverrideDataSerializer extends FioStructSerializer<ScnInstanceOverrideData> {
    public ScnInstanceOverrideDataSerializer() {
        super(ScnInstanceOverrideData::new, List.of(
            new FioStructMember<>("State", ScnInstanceOverrideData::setState, new FioBitSetFlagsSerializer()),
            new FioStructMember<>("Name", ScnInstanceOverrideData::setName, new FioStringSerializer()),
            new FioStructMember<>("Mat", ScnInstanceOverrideData::setMat, new MatrixSerializer()),
            new FioStructMember<>("Visible", ScnInstanceOverrideData::setVisible, new FioBoolSerializer()),
            new FioStructMember<>("Affixes", ScnInstanceOverrideData::setAffixes, new FioStringSerializer())
        ));
    }
}
