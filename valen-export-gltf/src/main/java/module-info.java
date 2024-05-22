module valen.export.gltf {
    requires com.google.gson;
    requires valen.core;

    requires static org.immutables.value;

    exports be.twofold.valen.export.gltf;

    opens be.twofold.valen.export.gltf.model to com.google.gson;
    exports be.twofold.valen.export.gltf.glb;
    exports be.twofold.valen.export.gltf.model;
}
