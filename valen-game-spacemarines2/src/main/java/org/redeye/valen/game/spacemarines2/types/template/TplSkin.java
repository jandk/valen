package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.math.*;

import java.util.*;

public class TplSkin {
    public BitSet state;
    public List<Short> skinCompoundList = null;
    public List<Integer> skinCompoundBoneSearchIndex = null;
    public List<Short> skinCompoundBoneIndices = null;
    public List<Matrix4> bonesExclMatrList = null;
    public List<Byte> bonesParentList = null;
    public int nBones = 0;
    public List<Matrix4> boneInvBindMatrList = null;
    public List<Short> lodBonesCount = null;

    public TplSkin() {
    }
}
