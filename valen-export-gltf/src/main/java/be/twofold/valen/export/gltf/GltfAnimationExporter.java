package be.twofold.valen.export.gltf;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.format.gltf.*;

import java.io.*;

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
        var skeletonMapper = new GltfSkeletonMapper(writer);
        var animationMapper = new GltfAnimationMapper(writer);
        skeletonMapper.map(animation.skeleton());
        var animationSchema = animationMapper.map(animation, animation.skeleton().bones().size());
        writer.addAnimation(animationSchema);
    }
}
