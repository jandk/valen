module valen.export.gltf {
    requires com.google.gson;
    requires valen.core;

    requires static org.immutables.value;
    requires java.desktop;

    exports be.twofold.valen.export.gltf.model to com.google.gson, org.immutables.value, valen.app;
    exports be.twofold.valen.export.gltf;
    exports be.twofold.valen.export.gltf.model.extensions.lightspunctual;

    opens be.twofold.valen.export.gltf.model to com.google.gson;
    opens be.twofold.valen.export.gltf.model.extensions.lightspunctual to com.google.gson;
    exports be.twofold.valen.export.gltf.model.extensions.collections;
    opens be.twofold.valen.export.gltf.model.extensions.collections to com.google.gson;
}
