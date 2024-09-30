package org.redeye.valen.game.spacemarines2.types.scene;

import org.redeye.valen.game.spacemarines2.serializers.template.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ScnScene {
    private TxmTex texList;
    private String psCache;
    private List<Pair<String, String>> setStrPairs;
    private GeometryManager geomManager;
    private TplExternData externData;
    private CdtColl pColl;
    private ScnBrandCache brandCache;
    private List<Integer> unkInts;
    private BitSet state;

    public String getPsCache() {
        return psCache;
    }

    public void setPsCache(String psCache) {
        this.psCache = psCache;
    }

    public List<Pair<String, String>> getSetStrPairs() {
        return setStrPairs;
    }

    public void setSetStrPairs(List<Pair<String, String>> setStrPairs) {
        this.setStrPairs = setStrPairs;
    }

    public GeometryManager getGeomManager() {
        return geomManager;
    }

    public void setGeomManager(GeometryManager geomManager) {
        this.geomManager = geomManager;
    }

    public TplExternData getExternData() {
        return externData;
    }

    public void setExternData(TplExternData externData) {
        this.externData = externData;
    }

    public CdtColl getpColl() {
        return pColl;
    }

    public void setpColl(CdtColl pColl) {
        this.pColl = pColl;
    }

    public ScnBrandCache getBrandCache() {
        return brandCache;
    }

    public void setBrandCache(ScnBrandCache brandCache) {
        this.brandCache = brandCache;
    }

    public List<Integer> getUnkInts() {
        return unkInts;
    }

    public void setUnkInts(List<Integer> unkInts) {
        this.unkInts = unkInts;
    }

    public TxmTex getTexList() {
        return texList;
    }

    public void setTexList(TxmTex texList) {
        this.texList = texList;
    }

    public BitSet getState() {
        return state;
    }

    public void setState(BitSet state) {
        this.state = state;
    }
}
