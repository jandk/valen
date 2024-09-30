package org.redeye.valen.game.spacemarines2.types.template;

import org.redeye.valen.game.spacemarines2.types.*;

public class ObjGeomUnshared {
    public Integer splitIndex;
    public Integer splitCount;
    public BBox bbox;
    public OBB obb;
    public Float sortingTranspDistOffset;
    public Integer causticVolIdx;
    public UvVertex[] texOffset;
    public RendLocalCubemapInfluence cubemapReflData;
    public Integer color;
    public Integer lastFameVisible;
    public Byte transp;
    public Byte lodTransp;
    public Byte cubemapRefrIdx;
    public Byte alphaKillValue;
    public Byte zBiasValue;
    public Byte renderPassId;
    public Byte blendRend;
    public Byte transpRendPriority;

    public void setSplitCount(Integer splitCount) {
        this.splitCount = splitCount;
    }

    public void setSplitIndex(Integer splitIndex) {
        this.splitIndex = splitIndex;
    }

    public void setBbox(BBox bbox) {
        this.bbox = bbox;
    }

    public void setObb(OBB obb) {
        this.obb = obb;
    }

    public void setSortingTranspDistOffset(Float sortingTranspDistOffset) {
        this.sortingTranspDistOffset = sortingTranspDistOffset;
    }

    public void setCausticVolIdx(Integer causticVolIdx) {
        this.causticVolIdx = causticVolIdx;
    }

    public void setTexOffset(UvVertex[] texOffset) {
        this.texOffset = texOffset;
    }

    public void setCubemapReflData(RendLocalCubemapInfluence cubemapReflData) {
        this.cubemapReflData = cubemapReflData;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public void setLastFameVisible(Integer lastFameVisible) {
        this.lastFameVisible = lastFameVisible;
    }

    public void setTransp(Byte transp) {
        this.transp = transp;
    }

    public void setLodTransp(Byte lodTransp) {
        this.lodTransp = lodTransp;
    }

    public void setCubemapRefrIdx(Byte cubemapRefrIdx) {
        this.cubemapRefrIdx = cubemapRefrIdx;
    }

    public void setAlphaKillValue(Byte alphaKillValue) {
        this.alphaKillValue = alphaKillValue;
    }

    public void setzBiasValue(Byte zBiasValue) {
        this.zBiasValue = zBiasValue;
    }

    public void setRenderPassId(Byte renderPassId) {
        this.renderPassId = renderPassId;
    }

    public void setBlendRend(Byte blendRend) {
        this.blendRend = blendRend;
    }

    public void setTranspRendPriority(Byte transpRendPriority) {
        this.transpRendPriority = transpRendPriority;
    }
}
