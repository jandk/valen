package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.types.spline.*;

public final class AnimRooted {
    Vector3 iniTranslation;
    Spline translation;
    Quaternion iniRotation;
    Spline rotation;

    public AnimRooted(
        Vector3 iniTranslation,
        Spline translation,
        Quaternion iniRotation,
        Spline rotation) {
        this.iniTranslation = iniTranslation;
        this.translation = translation;
        this.iniRotation = iniRotation;
        this.rotation = rotation;
    }

    public AnimRooted() {
        this(null, null, null, null);
    }

    public void setIniTranslation(Vector3 iniTranslation) {
        this.iniTranslation = iniTranslation;
    }

    public void setTranslation(Spline translation) {
        this.translation = translation;
    }

    public void setIniRotation(Quaternion iniRotation) {
        this.iniRotation = iniRotation;
    }

    public void setRotation(Spline rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return "AnimObjAnim[" +
            "iniTranslation=" + iniTranslation + ", " +
            "translation=" + translation + ", " +
            "iniRotation=" + iniRotation + ", " +
            "rotation=" + rotation + ']';
    }

}
