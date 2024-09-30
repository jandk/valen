package org.redeye.valen.game.spacemarines2.types.template;

import java.util.*;

public final class TplAnimTrack {
    public List<AnimSeq> seqList;
    public List<AnimObjAnim> objAnimList;
    public List<Short> objMapList;
    public AnimRooted rootAnim;

    public TplAnimTrack(
        List<AnimSeq> seqList,
        List<AnimObjAnim> objAnimList,
        List<Short> objMapList,
        AnimRooted rootAnim
    ) {
        this.seqList = seqList;
        this.objAnimList = objAnimList;
        this.objMapList = objMapList;
        this.rootAnim = rootAnim;
    }

    public TplAnimTrack() {
        this(null, null, null, null);
    }

    public void setSeqList(List<AnimSeq> v) {
        seqList = v;
    }

    public void setObjAnimList(List<AnimObjAnim> v) {
        objAnimList = v;
    }

    public void setObjMapList(List<Short> objMapList) {
        this.objMapList = objMapList;
    }

    public void setRootAnim(AnimRooted rootAnim) {
        this.rootAnim = rootAnim;
    }

    @Override
    public String toString() {
        return "TplAnimTrack[" +
            "seqList=" + seqList + ", " +
            "objAnimList=" + objAnimList + ", " +
            "objMapList=" + objMapList + ", " +
            "rootAnim=" + rootAnim + ", " + ']';
    }

}
