package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.math.*;

import java.util.*;

public class ObjObj {
    private String name;
    private String name2;

    private String ObjectName;

    private ObjGeomUnshared geomData;

    private String affixes;

    private String affixesVis;
    private Long stateUsr;
    private Set<ObjState> state;
    private Long stateProc;
    private Long stateRend;
    private Integer validFrame;
    private Integer setId;
    private int vBufVersion;
    private Long stateFiltr;
    private Short parentId;
    private Short nextId;
    private Short prevId;
    private Short childId;
    private Short lwiTpIdx;
    private Short id;
    private Short animNmb;
    private Matrix4 matrixLt;
    private Matrix4 modelMatrix;

    private String sourceId;

    @Override
    public String toString() {
        return "ObjObj{" +
            "name='" + getName() + '\'' +
            ", parentId=" + getParentId() +
            ", childId=" + getChildId() +
            ", nextId=" + getNextId() +
            ", prevId=" + getPrevId() +
            ", state=" + getState() +
            '}';
    }

    public Short getParentId() {
        return parentId;
    }

    public void setParentId(Short parentId) {
        this.parentId = parentId;
    }

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

    public String getObjectName() {
        return ObjectName;
    }

    public void setObjectName(String objectName) {
        ObjectName = objectName;
    }

    public ObjGeomUnshared getGeomData() {
        return geomData;
    }

    public void setGeomData(ObjGeomUnshared geomData) {
        this.geomData = geomData;
    }

    public String getAffixes() {
        return affixes;
    }

    public void setAffixes(String affixes) {
        this.affixes = affixes;
    }

    public String getAffixesVis() {
        return affixesVis;
    }

    public void setAffixesVis(String affixesVis) {
        this.affixesVis = affixesVis;
    }

    public Long getStateUsr() {
        return stateUsr;
    }

    public void setStateUsr(Long stateUsr) {
        this.stateUsr = stateUsr;
    }

    public Set<ObjState> getState() {
        return state;
    }

    public void setState(Set<ObjState> state) {
        this.state = state;
    }

    public Long getStateProc() {
        return stateProc;
    }

    public void setStateProc(Long stateProc) {
        this.stateProc = stateProc;
    }

    public Long getStateRend() {
        return stateRend;
    }

    public void setStateRend(Long stateRend) {
        this.stateRend = stateRend;
    }

    public Integer getValidFrame() {
        return validFrame;
    }

    public void setValidFrame(Integer validFrame) {
        this.validFrame = validFrame;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public int getvBufVersion() {
        return vBufVersion;
    }

    public void setvBufVersion(int vBufVersion) {
        this.vBufVersion = vBufVersion;
    }

    public Long getStateFiltr() {
        return stateFiltr;
    }

    public void setStateFiltr(Long stateFiltr) {
        this.stateFiltr = stateFiltr;
    }

    public Short getNextId() {
        return nextId;
    }

    public void setNextId(Short nextId) {
        this.nextId = nextId;
    }

    public Short getPrevId() {
        return prevId;
    }

    public void setPrevId(Short prevId) {
        this.prevId = prevId;
    }

    public Short getChildId() {
        return childId;
    }

    public void setChildId(Short childId) {
        this.childId = childId;
    }

    public Short getLwiTpIdx() {
        return lwiTpIdx;
    }

    public void setLwiTpIdx(Short lwiTpIdx) {
        this.lwiTpIdx = lwiTpIdx;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Short getAnimNmb() {
        return animNmb;
    }

    public void setAnimNmb(Short animNmb) {
        this.animNmb = animNmb;
    }

    public Matrix4 getMatrixLt() {
        return matrixLt;
    }

    public void setMatrixLt(Matrix4 matrix) {
        this.matrixLt = matrix;
    }

    public Matrix4 getModelMatrix() {
        return modelMatrix;
    }

    public void setModelMatrix(Matrix4 modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
