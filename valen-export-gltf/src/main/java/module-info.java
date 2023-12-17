module valen.export.gltf {
    requires com.google.gson;
    requires valen.core;

    exports be.twofold.valen.export.gltf.model to com.google.gson;
    exports be.twofold.valen.export.gltf;
}
