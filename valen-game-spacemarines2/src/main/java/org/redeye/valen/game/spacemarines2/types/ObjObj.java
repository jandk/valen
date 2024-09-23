package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.math.*;

import java.util.*;

public class ObjObj {
    public String name;
    public String name2;

    public String ObjectName;

    public ObjGeomUnshared geomData;

    public String affixes;

    public String affixesVis;
    public Long stateUsr;
    public Set<ObjState> state;
    public Long stateProc;
    public Long stateRend;
    public Integer validFrame;
    public Integer setId;
    public int vBufVersion;
    public Long stateFiltr;
    public Short parentId;
    public Short nextId;
    public Short prevId;
    public Short childId;
    public Short lwiTpIdx;
    public Short id;
    public Short animNmb;
    public Matrix4 matrixLt;
    public Matrix4 modelMatrix;

    public String sourceId;

    public void setName(String name) {
        this.name = name;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public void setObjectName(String objectName) {
        ObjectName = objectName;
    }

    public void setGeomData(ObjGeomUnshared geomData) {
        this.geomData = geomData;
    }

    public void setAffixes(String affixes) {
        this.affixes = affixes;
    }

    public void setAffixesVis(String affixesVis) {
        this.affixesVis = affixesVis;
    }

    public void setStateUsr(Long stateUsr) {
        this.stateUsr = stateUsr;
    }

    public void setState(Set<ObjState> state) {
        this.state = state;
    }

    public void setStateProc(Long stateProc) {
        this.stateProc = stateProc;
    }

    public void setStateRend(Long stateRend) {
        this.stateRend = stateRend;
    }

    public void setValidFrame(Integer validFrame) {
        this.validFrame = validFrame;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public void setvBufVersion(int vBufVersion) {
        this.vBufVersion = vBufVersion;
    }

    public void setStateFiltr(Long stateFiltr) {
        this.stateFiltr = stateFiltr;
    }

    public void setParentId(Short parentId) {
        this.parentId = parentId;
    }

    public void setNextId(Short nextId) {
        this.nextId = nextId;
    }

    public void setPrevId(Short prevId) {
        this.prevId = prevId;
    }

    public void setChildId(Short childId) {
        this.childId = childId;
    }

    public void setLwiTpIdx(Short lwiTpIdx) {
        this.lwiTpIdx = lwiTpIdx;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public void setAnimNmb(Short animNmb) {
        this.animNmb = animNmb;
    }

    public void setMatrixLt(Matrix4 matrix) {
        this.matrixLt = matrix;
    }

    public void setModelMatrix(Matrix4 modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString() {
        return "ObjObj{" +
            "name='" + name + '\'' +
            ", parentId=" + parentId +
            ", childId=" + childId +
            ", state=" + state +
            '}';
    }
}
