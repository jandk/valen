package be.twofold.valen.game.gbfr.util;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record FlatBufferTable(int offset, Shorts vTable) {
    public static FlatBufferTable root(BinarySource source) throws IOException {
        source.position(source.readInt());
        return read(source);
    }

    public static FlatBufferTable read(BinarySource source) throws IOException {
        var position = Math.toIntExact(source.position());
        var offset = source.position() - source.readInt();
        source.position(offset);
        var vTableSize = source.readShort();
        var objectSize = source.readShort();
        var vTable = source.readShorts((vTableSize - 4) / 2);
        return new FlatBufferTable(position, vTable);
    }

    public short readShort(BinarySource source, int index) throws IOException {
        var offset = offset(index);
        source.position(offset);
        return source.readShort();
    }

    public String readString(BinarySource source, int index) throws IOException {
        vector(source, index);
        return source.readString(StringFormat.INT_LENGTH);
    }

    public Ints readInts(BinarySource source, int index) throws IOException {
        vector(source, index);
        return source.readInts(source.readInt());
    }

    public Longs readLongs(BinarySource source, int index) throws IOException {
        vector(source, index);
        return source.readLongs(source.readInt());
    }

    public <T> List<T> readObjects(BinarySource source, int index, BinarySource.Mapper<T> mapper) throws IOException {
        vector(source, index);
        return source.readObjects(source.readInt(), mapper);
    }

    private void vector(BinarySource source, int index) throws IOException {
        var offset = offset(index);
        source.position(offset);
        source.position(offset + source.readInt());
    }

    private int offset(int index) throws IOException {
        int offset = vTable.get(index);
        if (offset == 0) {
            throw new IOException("Index " + index + " is empty");
        }
        return this.offset + offset;
    }
}
