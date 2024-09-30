package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.spline.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjPropBlendShapeAnimSerializer extends FioStructSerializer<ObjPropBlendShapeAnim> {
    public ObjPropBlendShapeAnimSerializer() {
        super(ObjPropBlendShapeAnim::new, 12, List.of(
            new FioStructMember<>("BlendShapeNames", ObjPropBlendShapeAnim::setBlendShapeNames, new FioArraySerializer<>(() -> "", 9, new FioStringSerializer())),
            new FioStructMember<>("BlendShapes", ObjPropBlendShapeAnim::setBlendShapes, new FioArraySerializer<>(Spline::new, 9, new SplineSerializer())),
            new FioStructMember<>("Wrinkles", ObjPropBlendShapeAnim::setWrinkles, new FioArraySerializer<>(Spline::new, 9, new SplineSerializer()))
        ));
    }
}
