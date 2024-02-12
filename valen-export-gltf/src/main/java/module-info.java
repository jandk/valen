module valen.export.gltf {
    requires com.google.gson;
    requires valen.core;
    requires static org.immutables.value;
    requires static org.immutables.gson;

    exports be.twofold.valen.export.gltf.model;
    exports be.twofold.valen.export.gltf;
    exports be.twofold.valen.export.gltf.model.extensions.lightspunctual;
}
