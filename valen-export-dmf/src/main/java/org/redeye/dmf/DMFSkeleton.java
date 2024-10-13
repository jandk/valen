package org.redeye.dmf;


import java.util.*;

public class DMFSkeleton {
    public final List<DMFBone> bones = new ArrayList<>();


    public DMFBone newBone(String name, DMFTransform transform, int parentId) {
        final DMFBone bone = new DMFBone(name, transform, parentId);
        bones.add(bone);
        return bone;
    }

    public DMFBone findBone(String name) {
        for (DMFBone bone : bones) {
            if (bone.name.equals(name)) {
                return bone;
            }
        }
        return null;
    }

    public int findBoneId(String name) {
        for (int i = 0; i < bones.size(); i++) {
            if (bones.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
