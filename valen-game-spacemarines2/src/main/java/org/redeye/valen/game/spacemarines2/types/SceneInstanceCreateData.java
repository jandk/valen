package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.psSection.*;

import java.util.*;

public class SceneInstanceCreateData {
    private String name;
    private String name2;
    private String nameTpl;
    private Matrix4 mat;
    private Vector3 scale;
    private String affixes;
    private PsSectionValue.PsSectionObject ps;
    private List<ScnInstanceOverrideData> overrides;
    private BitSet gameObjectFlags;
    private Integer parentInstIdx;
    private String parentObj;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getNameTpl() {
        return nameTpl;
    }

    public void setNameTpl(String nameTpl) {
        this.nameTpl = nameTpl;
    }

    public Matrix4 getMat() {
        return mat;
    }

    public void setMat(Matrix4 mat) {
        this.mat = mat;
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
    }

    public String getAffixes() {
        return affixes;
    }

    public void setAffixes(String affixes) {
        this.affixes = affixes;
    }

    public PsSectionValue.PsSectionObject getPs() {
        return ps;
    }

    public void setPs(PsSectionValue.PsSectionObject ps) {
        this.ps = ps;
    }

    public List<ScnInstanceOverrideData> getOverrides() {
        return overrides;
    }

    public void setOverrides(List<ScnInstanceOverrideData> overrides) {
        this.overrides = overrides;
    }

    public BitSet getGameObjectFlags() {
        return gameObjectFlags;
    }

    public void setGameObjectFlags(BitSet gameObjectFlags) {
        this.gameObjectFlags = gameObjectFlags;
    }

    public Integer getParentInstIdx() {
        return parentInstIdx;
    }

    public void setParentInstIdx(Integer parentInstIdx) {
        this.parentInstIdx = parentInstIdx;
    }

    public String getParentObj() {
        return parentObj;
    }

    public void setParentObj(String parentObj) {
        this.parentObj = parentObj;
    }

    @Override
    public String toString() {
        return "SceneInstanceCreateData{" +
            "name='" + name + '\'' +
            ", nameTpl='" + nameTpl + '\'' +
            ", parentInstIdx=" + parentInstIdx +
            ", parentObj='" + parentObj + '\'' +
            '}';
    }
}
