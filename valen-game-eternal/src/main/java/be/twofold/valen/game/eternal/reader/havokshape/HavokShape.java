package be.twofold.valen.game.eternal.reader.havokshape;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record HavokShape(
    List<HavokShapeInfo> infos,
    List<String> names,
    Bytes data
) {
    public static HavokShape read(BinaryReader reader) throws IOException {
        var infos = reader.readObjects(reader.readInt(), HavokShapeInfo::read);
        var names = reader.readObjects(reader.readInt(), bb -> readFixedString(bb, 1024));
        var data = reader.readBytes(reader.readInt());

        reader.expectEnd();

        return new HavokShape(infos, names, data);
    }

    private static String readFixedString(BinaryReader reader, int length) throws IOException {
        return reader.readString(length).trim();
    }

    public static void main(String[] args) throws IOException {
        var bytes = Files.readAllBytes(Path.of("D:\\Eternal\\DOOMExtracted\\maps\\game\\hub\\hub\\_combo\\world.hkshape"));

        var shape = HavokShape.read(BinaryReader.fromBytes(Bytes.wrap(bytes)));
        HkTagFile.read(shape.data().asBuffer());
    }
}
