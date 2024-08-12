package be.twofold.valen.reader.havokshape;

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
    public static HavokShape read(DataSource source) throws IOException {
        var infos = source.readStructs(source.readInt(), HavokShapeInfo::read);
        var names = source.readStructs(source.readInt(), bb -> readFixedString(bb, 1024));
        var data = source.readBytes(source.readInt());

        source.expectEnd();

        return new HavokShape(infos, names, data);
    }

    private static String readFixedString(DataSource source, int length) throws IOException {
        var bytes = source.readBytes(length);
        var index = 0;
        while (index < bytes.length && bytes[index] != 0) {
            index++;
        }
        return new String(bytes, 0, index, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws IOException {
        var bytes = Files.readAllBytes(Path.of("D:\\Eternal\\DOOMExtracted\\maps\\game\\hub\\hub\\_combo\\world.hkshape"));

        var buffer = new ByteArrayDataSource(bytes);
        var shape = HavokShape.read(buffer);

        HkTagFile.read(ByteBuffer.wrap(shape.data));
    }
}
