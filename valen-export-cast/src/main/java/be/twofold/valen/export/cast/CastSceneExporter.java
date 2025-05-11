package be.twofold.valen.export.cast;

import be.twofold.valen.core.scene.*;
import be.twofold.valen.export.cast.mappers.*;
import be.twofold.valen.format.cast.*;

import java.io.*;
import java.nio.file.*;

public final class CastSceneExporter extends CastExporter<Scene> {
    @Override
    public String getID() {
        return "scene.cast";
    }

    @Override
    public Class<Scene> getSupportedType() {
        return Scene.class;
    }

    @Override
    public void doExport(Scene scene, CastNode.Root root, Path castPath, Path imagePath) throws IOException {
        for (var instance : scene.instances()) {
            CastNode.Instance instanceNode = root.createInstance();
            var referenceFile = instanceNode.createFile()
                .setPath(instance.modelReference().filename() + ".cast");

            instanceNode
                .setName(instance.name())
                .setReferenceFile(referenceFile.hash())
                .setPosition(CastUtils.mapVector3(instance.translation()))
                .setRotation(CastUtils.mapQuaternion(instance.rotation()))
                .setScale(CastUtils.mapVector3(instance.scale()));
        }
    }
}
