package be.twofold.valen.export.gltf;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.node.*;

import java.io.*;
import java.util.*;

public final class GltfAnimationExporter extends GltfExporter<Animation> {
    @Override
    public String getID() {
        return "animation.gltf";
    }

    @Override
    public Class<Animation> getSupportedType() {
        return Animation.class;
    }

    @Override
    void doExport(Animation animation, GltfWriter writer) throws IOException {
        var skeleton = animation.skeleton();

        // Build the same skin/joint structure a skinned model would, so Blender imports an armature.
        // There is no model root here, so the joints hang off an up-axis node that becomes the scene
        // root; the skin (and the scene) are what let Blender turn the joints into a bone hierarchy.
        var skeletonMapper = new GltfSkeletonMapper(writer);
        var mappedSkin = skeletonMapper.mapSkin(skeleton);

        var rootNodeID = writer.addNode(ImmutableNode.builder()
            .rotation(GltfUtils.mapQuaternion(skeleton.upAxis().rotateTo(Axis.Y)))
            .addChildren(mappedSkin.rootJoint())
            .build());
        writer.addScene(List.of(rootNodeID));

        var animationMapper = new GltfAnimationMapper(writer);
        var animationSchema = animationMapper.map(animation, mappedSkin.jointNodeIDs());
        writer.addAnimation(animationSchema);
    }
}
