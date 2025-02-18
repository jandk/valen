module valen.format.gltf {
    requires com.google.gson;
    requires static org.immutables.value;

    exports be.twofold.valen.format.gltf.model.accessor;
    exports be.twofold.valen.format.gltf.model.animation;
    exports be.twofold.valen.format.gltf.model.asset;
    exports be.twofold.valen.format.gltf.model.buffer;
    exports be.twofold.valen.format.gltf.model.bufferview;
    exports be.twofold.valen.format.gltf.model.camera;
    exports be.twofold.valen.format.gltf.model.extension;
    exports be.twofold.valen.format.gltf.model.image;
    exports be.twofold.valen.format.gltf.model.material;
    exports be.twofold.valen.format.gltf.model.mesh;
    exports be.twofold.valen.format.gltf.model.node;
    exports be.twofold.valen.format.gltf.model.sampler;
    exports be.twofold.valen.format.gltf.model.scene;
    exports be.twofold.valen.format.gltf.model.skin;
    exports be.twofold.valen.format.gltf.model.texture;
    exports be.twofold.valen.format.gltf.model;
    exports be.twofold.valen.format.gltf.types;
    exports be.twofold.valen.format.gltf;

    opens be.twofold.valen.format.gltf.gson to com.google.gson;
    opens be.twofold.valen.format.gltf.model to com.google.gson;
    opens be.twofold.valen.format.gltf.model.accessor to com.google.gson;
    opens be.twofold.valen.format.gltf.model.animation to com.google.gson;
    opens be.twofold.valen.format.gltf.model.asset to com.google.gson;
    opens be.twofold.valen.format.gltf.model.buffer to com.google.gson;
    opens be.twofold.valen.format.gltf.model.bufferview to com.google.gson;
    opens be.twofold.valen.format.gltf.model.camera to com.google.gson;
    opens be.twofold.valen.format.gltf.model.extension to com.google.gson;
    opens be.twofold.valen.format.gltf.model.image to com.google.gson;
    opens be.twofold.valen.format.gltf.model.material to com.google.gson;
    opens be.twofold.valen.format.gltf.model.mesh to com.google.gson;
    opens be.twofold.valen.format.gltf.model.node to com.google.gson;
    opens be.twofold.valen.format.gltf.model.sampler to com.google.gson;
    opens be.twofold.valen.format.gltf.model.scene to com.google.gson;
    opens be.twofold.valen.format.gltf.model.skin to com.google.gson;
    opens be.twofold.valen.format.gltf.model.texture to com.google.gson;
    opens be.twofold.valen.format.gltf.types to com.google.gson;
}
