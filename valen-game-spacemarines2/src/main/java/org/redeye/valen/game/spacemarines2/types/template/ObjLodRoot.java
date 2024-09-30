package org.redeye.valen.game.spacemarines2.types.template;

import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class ObjLodRoot {
    public List<Integer> objIds;
    public List<Integer> maxObjLodIndices;
    public List<ObjLodDist> lodDists;

    public void setObjIds(List<Integer> objIds) {
        this.objIds = objIds;
    }

    public void setMaxObjLodIndices(List<Integer> maxObjLodIndices) {
        this.maxObjLodIndices = maxObjLodIndices;
    }

    public void setLodDists(List<ObjLodDist> lodDists) {
        this.lodDists = lodDists;
    }

    public void setBbox(BBox bbox) {
        this.bbox = bbox;
    }

    public void setDontApplyBias(Byte dontApplyBias) {
        this.dontApplyBias = dontApplyBias;
    }

    public BBox bbox;
    public Byte dontApplyBias;
}
