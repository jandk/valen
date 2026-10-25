package be.twofold.valen.export.gltf;

import be.twofold.valen.core.scene.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.mesh.*;
import be.twofold.valen.format.gltf.model.node.*;

import java.io.*;
import java.util.*;

public abstract class GltfSceneExporter extends GltfExporter<Scene> {
    GltfSceneExporter(GltfExportMode mode) {
        super(mode);
    }

    @Override
    public Class<Scene> getSupportedType() {
        return Scene.class;
    }

    @Override
    void doExport(Scene scene, GltfWriter writer) throws IOException {
        var modelMapper = new GltfModelSingleMapper(writer);
        var instanceNodes = new ArrayList<NodeID>();
        for (var instance : scene.instances()) {
            modelMapper.map(instance.modelReference())
                .ifPresent(meshID -> instanceNodes.add(mapInstance(writer, instance, meshID)));
        }

        writer.addScene(instanceNodes);
    }

    private NodeID mapInstance(GltfWriter writer, Instance instance, MeshID meshID) {
        return writer.addNode(ImmutableNode.builder()
            .name(Optional.ofNullable(instance.name()))
            .translation(GltfUtils.mapVector3(instance.translation()))
            .rotation(GltfUtils.mapQuaternion(instance.rotation()))
            .scale(GltfUtils.mapVector3(instance.scale()))
            .mesh(meshID)
            .build());
    }

    public static final class Binary extends GltfSceneExporter {
        public Binary() {
            super(GltfExportMode.GLB);
        }

        @Override
        public String getID() {
            return "scene.glb";
        }
    }

    public static final class Split extends GltfSceneExporter {
        public Split() {
            super(GltfExportMode.GLTF_SPLIT);
        }

        @Override
        public String getID() {
            return "scene.gltf";
        }
    }
}
