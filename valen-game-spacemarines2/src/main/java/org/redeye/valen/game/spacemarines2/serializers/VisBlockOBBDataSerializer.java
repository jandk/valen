package org.redeye.valen.game.spacemarines2.serializers;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class VisBlockOBBDataSerializer extends FioStructSerializer<VisBlockOBBData> {
    public VisBlockOBBDataSerializer() {
        super(VisBlockOBBData::new, List.of(
            new FioStructMember<>("VisBox", VisBlockOBBData::setVisBox, new BBoxSerializer()),
            new FioStructMember<>("MaxHideDist2", VisBlockOBBData::setMaxHideDist2, new FioFloatSerializer()),
            new FioStructMember<>("MaxSMFactor2", VisBlockOBBData::setMaxHideDist2, new FioFloatSerializer())

        ));
    }
}
