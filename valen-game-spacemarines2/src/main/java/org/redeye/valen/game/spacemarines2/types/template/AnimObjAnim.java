package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.types.spline.*;

public final class AnimObjAnim {
    Vector3 iniTranslation;
    Spline translation;
    Quaternion iniRotation;
    Spline rotation;
    Vector3 iniScale;
    Spline scale;
    Float iniVisibility;
    Spline visibility;

    public AnimObjAnim(
        Vector3 iniTranslation,
        Spline translation,
        Quaternion iniRotation,
        Spline rotation,
        Vector3 iniScale,
        Spline scale,
        Float iniVisibility,
        Spline visibility) {
        this.iniTranslation = iniTranslation;
        this.translation = translation;
        this.iniRotation = iniRotation;
        this.rotation = rotation;
        this.iniScale = iniScale;
        this.scale = scale;
        this.iniVisibility = iniVisibility;
        this.visibility = visibility;
    }

    public AnimObjAnim() {
        this(null, null, null, null, null, null, null, null);
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

    public void setIniScale(Vector3 iniScale) {
        this.iniScale = iniScale;
    }

    public void setScale(Spline scale) {
        this.scale = scale;
    }

    public void setIniVisibility(Float iniVisibility) {
        this.iniVisibility = iniVisibility;
    }

    public void setVisibility(Spline visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "AnimObjAnim[" +
            "iniTranslation=" + iniTranslation + ", " +
            "translation=" + translation + ", " +
            "iniRotation=" + iniRotation + ", " +
            "rotation=" + rotation + ", " +
            "iniScale=" + iniScale + ", " +
            "scale=" + scale + ", " +
            "iniVisibility=" + iniVisibility + ", " +
            "visibility=" + visibility + ']';
    }

}
