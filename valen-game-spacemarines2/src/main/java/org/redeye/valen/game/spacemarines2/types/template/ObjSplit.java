package org.redeye.valen.game.spacemarines2.types.template;

import java.util.*;

public class ObjSplit {
    public int numInst;
    public int startVert;
    public int nVert;
    public int startFace;
    public int nFace;
    public Map<Integer, Integer> texCoordMaxTile = new HashMap<>();
    public VertCompressParams vertCompParams = new VertCompressParams();
    public short skinCompoundId;
    public MtlDesc mtlDesc = new MtlDesc();
    public Map<String, Object> materialInfo = new HashMap<>();
    //    public dsSERDE_UNIQUE_PTR<vidPASS_OBJ> passDesc;
    public MtlTexDensity texDensity = new MtlTexDensity();
    public ObjGeom geom;

    @Override
    public String toString() {
        return "ObjSplit{" +
            "startVert=" + startVert +
            ", nVert=" + nVert +
            ", startFace=" + startFace +
            ", nFace=" + nFace +
            ", texCoordMaxTile=" + texCoordMaxTile +
            ", vertCompParams=" + vertCompParams +
            ", skinCompoundId=" + skinCompoundId +
            ", geom=" + geom +
            '}';
    }
}
