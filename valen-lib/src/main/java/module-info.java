module valen.lib {
    requires com.google.gson;
    requires com.sun.jna;
    requires java.sql;
    requires valen.core;

    exports be.twofold.valen.manager;
    exports be.twofold.valen.oodle;
    exports be.twofold.valen.reader.image;
    exports be.twofold.valen.reader.md6;
    exports be.twofold.valen.reader.md6anim;
    exports be.twofold.valen.reader.md6skl;
    exports be.twofold.valen.reader.model;
    exports be.twofold.valen.reader.packagemapspec;
    exports be.twofold.valen.reader.resource;
    exports be.twofold.valen.reader.streamdb;
    exports be.twofold.valen.resource;
}
