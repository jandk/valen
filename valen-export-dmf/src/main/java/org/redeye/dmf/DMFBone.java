package org.redeye.dmf;


public class DMFBone {
    public final String name;
    public DMFTransform transform;
    public final int parentId;
    public boolean localSpace;

    public DMFBone(String name, DMFTransform transform, int parentId) {
        this.name = name;
        this.transform = transform;
        this.parentId = parentId;
    }
}
