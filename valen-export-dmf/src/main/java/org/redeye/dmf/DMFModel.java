package org.redeye.dmf;



public class DMFModel extends DMFNode {
    public final DMFMesh mesh;
    public Integer skeletonId;

    public DMFModel( String name,  DMFMesh mesh) {
        super(name, DMFNodeType.MODEL);
        this.mesh = mesh;
    }

    public void setSkeleton( DMFSkeleton skeleton,  DMFSceneFile scene) {
        if (!scene.skeletons.contains(skeleton))
            scene.skeletons.add(skeleton);
        skeletonId = scene.skeletons.indexOf(skeleton);
    }
}
