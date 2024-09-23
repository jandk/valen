module valen.gltf {
    requires com.google.gson;
    requires static org.immutables.value;

    exports be.twofold.valen.gltf.model.accessor;
    exports be.twofold.valen.gltf.model.animation;
    exports be.twofold.valen.gltf.model.asset;
    exports be.twofold.valen.gltf.model.buffer;
    exports be.twofold.valen.gltf.model.camera;
    exports be.twofold.valen.gltf.model.image;
    exports be.twofold.valen.gltf.model.material;
    exports be.twofold.valen.gltf.model.mesh;
    exports be.twofold.valen.gltf.model.node;
    exports be.twofold.valen.gltf.model.sampler;
    exports be.twofold.valen.gltf.model.scene;
    exports be.twofold.valen.gltf.model.skin;
    exports be.twofold.valen.gltf.model.texture;
    exports be.twofold.valen.gltf.model;
    exports be.twofold.valen.gltf.types;
    exports be.twofold.valen.gltf;

    opens be.twofold.valen.gltf.model to com.google.gson;
    opens be.twofold.valen.gltf.model.accessor to com.google.gson;
    opens be.twofold.valen.gltf.model.animation to com.google.gson;
    opens be.twofold.valen.gltf.model.asset to com.google.gson;
    opens be.twofold.valen.gltf.model.buffer to com.google.gson;
    opens be.twofold.valen.gltf.model.camera to com.google.gson;
    opens be.twofold.valen.gltf.model.image to com.google.gson;
    opens be.twofold.valen.gltf.model.material to com.google.gson;
    opens be.twofold.valen.gltf.model.mesh to com.google.gson;
    opens be.twofold.valen.gltf.model.node to com.google.gson;
    opens be.twofold.valen.gltf.model.sampler to com.google.gson;
    opens be.twofold.valen.gltf.model.scene to com.google.gson;
    opens be.twofold.valen.gltf.model.skin to com.google.gson;
    opens be.twofold.valen.gltf.model.texture to com.google.gson;
    opens be.twofold.valen.gltf.types to com.google.gson;
}
