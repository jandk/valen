module valen.export.gltf {
    requires com.google.gson;
    requires static org.immutables.value;
    requires valen.core;

    exports be.twofold.valen.export.gltf.model to com.google.gson, org.immutables.value, valen.app;
    exports be.twofold.valen.export.gltf;
    exports be.twofold.valen.export.gltf.model.extensions.lightspunctual;

    opens be.twofold.valen.export.gltf.model to com.google.gson;
    opens be.twofold.valen.export.gltf.model.extensions.lightspunctual to com.google.gson;
}
