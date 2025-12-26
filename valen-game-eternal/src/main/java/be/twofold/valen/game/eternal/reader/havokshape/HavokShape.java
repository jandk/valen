package be.twofold.valen.game.eternal.reader.havokshape;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record HavokShape(
    List<HavokShapeInfo> infos,
    List<String> names,
    Bytes data
) {
    public static HavokShape read(BinarySource source) throws IOException {
        var infos = source.readObjects(source.readInt(), HavokShapeInfo::read);
        var names = source.readObjects(source.readInt(), bb -> readFixedString(bb, 1024));
        var data = source.readBytes(source.readInt());

        source.expectEnd();

        return new HavokShape(infos, names, data);
    }

    private static String readFixedString(BinarySource source, int length) throws IOException {
        return source.readString(length).trim();
    }

    public static void main(String[] args) throws IOException {
        var bytes = Files.readAllBytes(Path.of("D:\\Eternal\\DOOMExtracted\\maps\\game\\hub\\hub\\_combo\\world.hkshape"));

        var shape = HavokShape.read(BinarySource.wrap(Bytes.wrap(bytes)));
        HkTagFile.read(shape.data().asBuffer());
    }
}
