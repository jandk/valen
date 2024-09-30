package org.redeye.valen.game.spacemarines2.types.template;

import org.redeye.valen.game.spacemarines2.serializers.template.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public final class AnimTemplate {
    public String name;
    public String nameClass;
    public String affixes;
    public String ps;
    public TplSkin skin;
    public TplAnimTrack trackAnim;
    public BitSet state;
    public BBox bbox = null;
    public List<LodDef> lodDef;
    public TxmTex texList;
    public GeometryManager geometryManager;
    public TplExternData externData;


    public AnimTemplate() {
        geometryManager = new GeometryManager();
    }

    public void setName(String v) {
        name = v;
    }

    public void setNameClass(String v) {
        nameClass = v;
    }

    public void setState(BitSet v) {
        state = v;
    }

    public void setAffix(String v) {
        affixes = v;
    }

    public void setPs(String v) {
        ps = v;
    }

    public void setSkin(TplSkin v) {
        skin = v;
    }

    public void setBBox(BBox v) {
        bbox = v;
    }

    public void setTplAnimTrack(TplAnimTrack v) {
        trackAnim = v;
    }

    public void setLodDef(List<LodDef> lodDef) {
        this.lodDef = lodDef;
    }

    public void setTexList(TxmTex texList) {
        this.texList = texList;
    }

    public void setGeometryManager(GeometryManager geometryManager) {
        this.geometryManager = geometryManager;
    }

    public void setExternData(TplExternData externData) {
        this.externData = externData;
    }
}
