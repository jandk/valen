package org.redeye.valen.game.spacemarines2.types;

public class BlockInfo {
    private Integer globalInstId;
    private Integer tplIdxNInst;
    private Integer transIdxLwiIdx;
    private Integer pivotIdx;
    private Integer presetGridIdxHigh;
    private Integer presetGridIdxLow;
    private Float scaleCorrection;

    public Integer getGlobalInstId() {
        return globalInstId;
    }

    public void setGlobalInstId(Integer globalInstId) {
        this.globalInstId = globalInstId;
    }

    public Integer getTplIdxNInst() {
        return tplIdxNInst;
    }

    public void setTplIdxNInst(Integer tplIdxNInst) {
        this.tplIdxNInst = tplIdxNInst;
    }

    public Integer getTransIdxLwiIdx() {
        return transIdxLwiIdx;
    }

    public void setTransIdxLwiIdx(Integer transIdxLwiIdx) {
        this.transIdxLwiIdx = transIdxLwiIdx;
    }

    public Integer getPivotIdx() {
        return pivotIdx;
    }

    public void setPivotIdx(Integer pivotIdx) {
        this.pivotIdx = pivotIdx;
    }

    public Integer getPresetGridIdxHigh() {
        return presetGridIdxHigh;
    }

    public void setPresetGridIdxHigh(Integer presetGridIdxHigh) {
        this.presetGridIdxHigh = presetGridIdxHigh;
    }

    public Integer getPresetGridIdxLow() {
        return presetGridIdxLow;
    }

    public void setPresetGridIdxLow(Integer presetGridIdxLow) {
        this.presetGridIdxLow = presetGridIdxLow;
    }

    public Float getScaleCorrection() {
        return scaleCorrection;
    }

    public void setScaleCorrection(Float scaleCorrection) {
        this.scaleCorrection = scaleCorrection;
    }
}
