package org.redeye.valen.game.spacemarines2.types;

import org.redeye.valen.game.spacemarines2.types.spline.*;

import java.util.*;

public class ObjPropBlendShapeAnim implements ObjProp {
    public List<String> blendShapeNames;
    public List<Spline> blendShapes;
    public List<Spline> wrinkles;

    public void setWrinkles(List<Spline> wrinkles) {
        this.wrinkles = wrinkles;
    }

    public void setBlendShapes(List<Spline> blendShapes) {
        this.blendShapes = blendShapes;
    }

    public void setBlendShapeNames(List<String> blendShapeNames) {
        this.blendShapeNames = blendShapeNames;
    }

    @Override
    public String toString() {
        return "ObjPropBlendShapeAnim{" +
            "blendShapeNames=" + blendShapeNames +
            ", blendShapes=" + blendShapes +
            ", wrinkles=" + wrinkles +
            '}';
    }
}
