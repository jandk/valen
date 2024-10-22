package org.redeye.valen.game.spacemarines2.types.terrain;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;

import java.io.*;
import java.util.*;

public record TerrainBlockArray<T extends Number>(
    short defaultVal,
    int lengthX,
    int lengthZ,
    int texelsPerBlock,
    List<T> buffer,
    BitSet blocksStates
) {

    public static TerrainBlockArray<Short> readShort(DataSource source) throws IOException {
        short defaultVal = source.readShort();
        int lengthX = source.readInt();
        int lengthZ = source.readInt();
        int texelsPerBlock = source.readInt();
        var buffer = new FioArraySerializer<>(() -> (short) 0, new FioInt16Serializer()).load(source);
        var blocksStates = new FioBitSetSerializer().load(source);


        return new TerrainBlockArray<>(defaultVal, lengthX, lengthZ, texelsPerBlock, buffer, blocksStates);
    }

    public static TerrainBlockArray<Byte> readByte(DataSource source) throws IOException {
        short defaultVal = source.readByte();
        int lengthX = source.readInt();
        int lengthZ = source.readInt();
        int texelsPerBlock = source.readInt();
        var buffer = new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer()).load(source);
        var blocksStates = new FioBitSetSerializer().load(source);

        return new TerrainBlockArray<>(defaultVal, lengthX, lengthZ, texelsPerBlock, buffer, blocksStates);
    }
}
