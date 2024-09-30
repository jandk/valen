package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class AnimRootedSerializer extends FioStructSerializer<AnimRooted> {
    public AnimRootedSerializer() {
        super(AnimRooted::new, 12, List.of(
            new FioStructMember<>("IniTranslation", AnimRooted::setIniTranslation, new FioVec3Serializer()),
            new FioStructMember<>("Translation", AnimRooted::setTranslation, new SplineSerializer()),
            new FioStructMember<>("IniRotation", AnimRooted::setIniRotation, new FioQuatSerializer()),
            new FioStructMember<>("Rotation", AnimRooted::setRotation, new SplineSerializer())
        ));
    }
}
