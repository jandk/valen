package org.redeye.dmf;



public class DMFCompositeModel extends DMFNode {
    public final int skeletonId;

    public DMFCompositeModel( String name, int skeletonId) {
        super(name, DMFNodeType.SKINNED_MODEL);
        this.skeletonId = skeletonId;
    }
}
