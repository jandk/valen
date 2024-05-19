module valen.lib {
    requires com.google.gson;
    requires com.sun.jna;
    requires dagger;
    requires java.desktop; // For testing only
    requires java.sql; // For import only
    requires valen.core;

    exports be.twofold.valen.compression;
    exports be.twofold.valen.converter.decoder;
    exports be.twofold.valen.hash;
    exports be.twofold.valen.manager;
    exports be.twofold.valen.reader.binaryfile.blang;
    exports be.twofold.valen.reader.decl.parser;
    exports be.twofold.valen.reader.decl.renderparm;
    exports be.twofold.valen.reader.decl;
    exports be.twofold.valen.reader.file;
    exports be.twofold.valen.reader.filecompressed.entities;
    exports be.twofold.valen.reader.filecompressed;
    exports be.twofold.valen.reader.image;
    exports be.twofold.valen.reader.md6anim;
    exports be.twofold.valen.reader.md6model;
    exports be.twofold.valen.reader.md6skel;
    exports be.twofold.valen.reader.packagemapspec;
    exports be.twofold.valen.reader.resource;
    exports be.twofold.valen.reader.staticmodel;
    exports be.twofold.valen.reader.streamdb;
    exports be.twofold.valen.reader;
    exports be.twofold.valen.resource;
    exports be.twofold.valen.stream;
    exports be.twofold.valen.util;
    exports be.twofold.valen.writer.dds;
    exports be.twofold.valen.writer.png;
}
