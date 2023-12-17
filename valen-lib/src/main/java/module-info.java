module valen.lib {
    requires com.google.gson;
    requires com.sun.jna;
    requires java.sql;
    requires valen.core;

    exports be.twofold.valen.manager;
    exports be.twofold.valen.resource;
    exports be.twofold.valen.writer.gltf.model to com.google.gson;
}
