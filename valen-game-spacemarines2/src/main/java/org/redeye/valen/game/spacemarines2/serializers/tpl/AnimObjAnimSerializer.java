package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class AnimObjAnimSerializer extends FioStructSerializer<AnimObjAnim> {
    public AnimObjAnimSerializer() {
        super(AnimObjAnim::new, 12, List.of(
            new FioStructMember<>("IniTranslation", AnimObjAnim::setIniTranslation, new FioVec3Serializer()),
            new FioStructMember<>("Translation", AnimObjAnim::setTranslation, new SplineSerializer()),
            new FioStructMember<>("IniRotation", AnimObjAnim::setIniRotation, new FioQuatSerializer()),
            new FioStructMember<>("Rotation", AnimObjAnim::setRotation, new SplineSerializer()),
            new FioStructMember<>("IniScale", AnimObjAnim::setIniScale, new FioVec3Serializer()),
            new FioStructMember<>("Scale", AnimObjAnim::setScale, new SplineSerializer()),
            new FioStructMember<>("IniVisibility", AnimObjAnim::setIniVisibility, new FioFloatSerializer()),
            new FioStructMember<>("Visibility", AnimObjAnim::setVisibility, new SplineSerializer())
        ));
    }
}
