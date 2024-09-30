package org.redeye.valen.game.spacemarines2.types.template;


public final class LodDef {
    public Short objId;
    public Byte index;
    public Byte isLastLodUpToInfinity;

    public LodDef(
        Short objId,
        Byte index,
        Byte isLastLodUpToInfinity
    ) {
        this.objId = objId;
        this.index = index;
        this.isLastLodUpToInfinity = isLastLodUpToInfinity;
    }

    public LodDef() {
        this(null, null, null);
    }

    public void setObjId(Short objId) {
        this.objId = objId;
    }

    public void setIndex(Byte index) {
        this.index = index;
    }

    public void setIsLastLodUpToInfinity(Byte isLastLodUpToInfinity) {
        this.isLastLodUpToInfinity = isLastLodUpToInfinity;
    }

    @Override
    public String toString() {
        return "LodDef[" +
            "objId=" + objId + ", " +
            "index=" + index + ", " +
            "isLastLodUpToInfinity=" + isLastLodUpToInfinity + ']';
    }

}
