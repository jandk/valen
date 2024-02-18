module valen.export.gltf {
    requires com.google.gson;
    requires static org.immutables.value;
    requires valen.core;

    exports be.twofold.valen.export.gltf.model to com.google.gson, org.immutables.value;
    exports be.twofold.valen.export.gltf;

    opens be.twofold.valen.export.gltf.model to com.google.gson;
}
