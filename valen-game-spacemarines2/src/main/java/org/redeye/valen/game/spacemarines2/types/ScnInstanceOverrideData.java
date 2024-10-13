package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.math.*;

import java.util.*;

public class ScnInstanceOverrideData {
    private BitSet state;
    private String name;
    private Matrix4 mat;
    private Boolean visible;
    private String affixes;

    public BitSet getState() {
        return state;
    }

    public void setState(BitSet state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Matrix4 getMat() {
        return mat;
    }

    public void setMat(Matrix4 mat) {
        this.mat = mat;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getAffixes() {
        return affixes;
    }

    public void setAffixes(String affixes) {
        this.affixes = affixes;
    }
}
