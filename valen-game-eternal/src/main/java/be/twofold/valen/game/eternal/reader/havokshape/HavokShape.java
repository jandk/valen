package be.twofold.valen.game.eternal.reader.havokshape;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public record HavokShape(
    List<HavokShapeInfo> infos,
    List<String> names,
    byte[] data
) {
    public static HavokShape read(BinaryReader reader) throws IOException {
        var infos = reader.readObjects(reader.readInt(), HavokShapeInfo::read);
        var names = reader.readObjects(reader.readInt(), bb -> readFixedString(bb, 1024));
        var data = reader.readBytes(reader.readInt());

        reader.expectEnd();

        return new HavokShape(infos, names, data);
    }

    private static String readFixedString(BinaryReader reader, int length) throws IOException {
        var bytes = reader.readBytes(length);
        var index = 0;
        while (index < bytes.length && bytes[index] != 0) {
            index++;
        }
        return new String(bytes, 0, index, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws IOException {
        var bytes = Files.readAllBytes(Path.of("D:\\Eternal\\DOOMExtracted\\maps\\game\\hub\\hub\\_combo\\world.hkshape"));

        var shape = HavokShape.read(BinaryReader.fromArray(bytes));
        HkTagFile.read(ByteBuffer.wrap(shape.data()));
    }
}
