module valen.gltf {
    requires com.google.gson;
    requires static org.immutables.value;

    exports be.twofold.valen.gltf.model;
    exports be.twofold.valen.gltf.types;
    exports be.twofold.valen.gltf;

    opens be.twofold.valen.gltf.model to com.google.gson;
}
